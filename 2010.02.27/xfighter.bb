;Graphics3D(800, 600, 16, 1) ;Nustatome skiriamaja geba
;SetBuffer(BackBuffer())		 ;Parengiame vaizdo atminti


Global info1$ = "Artûras"
Global info2$ = "Autorius: Arturas"
Global info3$ = "Plodikas"
Global info4$ = "Gaidys"

Include "start.bb"

Global use_fog = True
Global terr, coin
Global tree_sprite, spark_sprite, bull_sprite
Global speed#
Global score
Global laikas = 600000
Global shoot = Load3DSound("./sounds/shoot.wav")
Global boom = Load3DSound("./sounds/boom.wav")
Global touch = LoadSound("./sounds/riverside.mp3")
SoundVolume(touch, 1)
PlaySound(touch)
SoundVolume(boom, 1)
SoundVolume(shoot, 1)
Type Player
	Field entity, camera
	Field ctrl_mode, cam_mode, ignition
	Field pitch#, yaw#, pitch_speed#, yaw_speed#, roll#, thrust#
	Field t_p#, t_y#, t_r#		; LEKTUVO PASUKIMAI: t_p - X asimi, t_y - Y asimi, t_r - Z asimi
	Field max_speed#					; MAKSIMALUS GREITIS
	Field vertikalus_greitis# ; LEKTUVO KILIMO (LEIDIMOSI) GREITIS
	Field kritimo_greitis#    ; LEKTUVO KRITIMO GREITIS
	Field flight_state$				; LEKTUVO SRYDZIO BUSENA (ant zemes/skrenda)
End Type

Type Spark
	Field alpha#, sprite
End Type
	
Type Bullet
	Field rot#, sprite
End Type

Const type_player = 1, type_bullet = 3,  type_ground = 10, type_tree = 5, type_item = 7
Collisions(1, 10, 2, 2)		  ; SUSIDURIMAI TARP LEKTUVO(1) IR ZEMES(10)
Collisions(1, 5, 1, 2)			; SUSIDURIMAI TARP LEKTUVO(1) IR MEDZIO(5)
Collisions(3, 7, 2, 2)			; SUSIDURIMAI TARP KULKOS(3) IR MONETOS(5)
Collisions(3, 10, 2, 2)		  ; SUSIDURIMAI TARP KULKOS(3) IR ZEMES(10)

Setup()
CreateScene()

plane = LoadAirPlane("./models/fighter1.3ds", "./models/plane1.jpg")
player1.Player = CreatePlayer(plane, 0 , 0, GraphicsWidth(), GraphicsHeight(), 1)

listener = CreateListener(player1\entity, 0.1, 1, 0.2)

time = MilliSecs()
elapsed = 0
While (Not KeyHit(1)) And (elapsed < laikas)
	elapsed = MilliSecs() - time
	UpdateGame()
	UpdateWorld
		
	RenderWorld tween

  Color(0, 0, 0)
	Text (0,  0,"Plane X: " + EntityX(player1\entity))
	Text (0, 10,"Plane Y: " + EntityY(player1\entity))
	Text (0, 20,"Plane Z: " + EntityZ(player1\entity))
	
	;I EKRANA ISVESTI LEKTUVO GREICIUS
	Text(0, 40, "PLANE THRUST: " + (player1\thrust * 100) + " km/h")      ; JUDEJIMO
	Text(0, 50,"Plane VSPEED: " + (player1\vertikalus_greitis) + " km/h")	; VERIKALUS	
	Text(0, 60,"Plane FSPEED: " + (player1\kritimo_greitis) + " km/h")		; KRITIMO

	;I EKRANA ISVESTI LEKTUVO BUSENA
	Text(0, 80,"Plane FSTATE: " + player1\flight_state)
	Text(700, 10,"SCORE: " + score)
	Text(700, 0, "TIME LEFT: " + (laikas - elapsed)/1000 + "s")
	
	Flip
Wend
	GamerOver()
End

Function UpdateGame()
	For b.Bullet=Each Bullet
		UpdateBullet(b)
	Next
	For s.Spark=Each Spark
		UpdateSpark(s)
	Next
	For p.Player=Each Player
		UpdatePlayer(p)
	Next
End Function

Function UpdatePlayer( p.Player )
	Local x_dir,y_dir,z_dir
	Select p\ctrl_mode
	Case 1:
		If KeyDown(203) x_dir=-1
		If KeyDown(205) x_dir=1
		
		If KeyDown(200) y_dir=-1
		If KeyDown(208) y_dir=1
		
		If KeyDown(30) z_dir=1
		If KeyDown(44) z_dir=-1
		
		If KeyHit(59) p\cam_mode=1
		If KeyHit(60) p\cam_mode=2
		If KeyHit(61) p\cam_mode=3
		If KeyHit(62) p\cam_mode=4

		If KeyHit(29)
			CreateBullet(p)
			;CreateBullet2(p)
		EndIf 
		
	Case 2:
		x_dir=JoyXDir()
		y_dir=JoyYDir()
		If JoyDown(1) z_dir=1
		If JoyDown(2) z_dir=-1
		
		If KeyHit(63) p\cam_mode=1
		If KeyHit(64) p\cam_mode=2
		If KeyHit(65) p\cam_mode=3
		If KeyHit(66) p\cam_mode=4
		
	End Select

	If x_dir<0
		p\yaw_speed=p\yaw_speed + (4-p\yaw_speed)*.04
	Else If x_dir>0
		p\yaw_speed=p\yaw_speed + (-4-p\yaw_speed)*.04
	Else
		p\yaw_speed=p\yaw_speed + (-p\yaw_speed)*.02
	EndIf
		
	If y_dir<0
		p\pitch_speed=p\pitch_speed + (2-p\pitch_speed)*.2
	Else If y_dir>0
		p\pitch_speed=p\pitch_speed + (-2-p\pitch_speed)*.2
	Else
		p\pitch_speed=p\pitch_speed + (-p\pitch_speed)*.1
	EndIf
		
	p\yaw=p\yaw+p\yaw_speed
	If p\yaw<-180 Then p\yaw=p\yaw+360
	If p\yaw>=180 Then p\yaw=p\yaw-360
	
	p\pitch=p\pitch+p\pitch_speed
	If p\flight_state = "touching ground" And p\pitch<-30 Then p\pitch = -30
	If p\flight_state = "touching ground" And p\pitch>10 Then p\pitch = 10
	If p\pitch<-180 Then p\pitch=p\pitch+360
	If p\pitch>=180 Then p\pitch=p\pitch-360

	p\roll=p\yaw_speed*30
	RotateEntity p\entity,p\pitch,p\yaw,p\roll
	
	;see if y/p/r funcs are working...
	p\t_p# = EntityPitch(p\entity)
	p\t_y# = EntityYaw(p\entity)
	p\t_r# = EntityRoll(p\entity)
	RotateEntity p\entity, p\t_p, p\t_y, p\t_r
	
	If p\ignition
		If z_dir>0			  ;JEI NUSPAUSTAS AKSELERATORIUS
			p\thrust=p\thrust + ((p\max_speed/100)-p\thrust)*.01 ; VX/S atitinka 100 KM/H
		Else If z_dir<0		;JEI NUSPAUSTAS STABDIS
			p\thrust=p\thrust + (-p\thrust)*.01
		EndIf
		MoveEntity p\entity,0,0,p\thrust
	Else If z_dir>0
		p\ignition=True
	EndIf
 ;--------------------------------------------------------------------------
 ; ZEMES TRAUKOS POVEIKIS
  p\vertikalus_greitis = p\thrust * Sin((-1)* p\t_p)

	speed = p\vertikalus_greitis

	If(p\vertikalus_greitis < 0.3) Then					; JEIGU LEKTUVAS NEATSVERIA ZEMES TRAUKOS JEGOS
		If(p\flight_state <> "touching ground") Then
			p\kritimo_greitis = p\kritimo_greitis + 0.01 	; Kritimo greitis DIDEJA
		Else
			p\kritimo_greitis = 0
		EndIf
	Else
		If(p\kritimo_greitis > 0) Then
			p\kritimo_greitis  = p\kritimo_greitis - p\vertikalus_greitis/50	; Kritimo greitis PASTOVUS
		EndIf
	EndIf

	TranslateEntity(p\entity, 0, (-1)*p\kritimo_greitis, 0) ;TEMPTI LEKTUVA ÞEMYN GREICIU: kritimo_greitis

	;LEKTUVO BUSENOS
	If (EntityCollided(p\entity, 10)) Then
		p\flight_state = "touching ground"
	Else
	  If((p\vertikalus_greitis - p\kritimo_greitis) < 0) Then
			p\flight_state = "losing altitude"
	 Else
			p\flight_state = "gaining altitude"
	 	EndIf
	EndIf

 ;--------------------------------------------------------------------------
	If p\camera
		Select p\cam_mode
		Case 1:
			EntityParent p\camera,p\entity
			RotateEntity p\camera,0,p\yaw,0,True
			PositionEntity p\camera,EntityX(p\entity),EntityY(p\entity),EntityZ(p\entity),True
			MoveEntity p\camera,0,1,-10
			PointEntity p\camera,p\entity,p\roll/2
		Case 2:
			EntityParent p\camera,0
			PositionEntity p\camera,EntityX(p\entity),EntityY(p\entity),EntityZ(p\entity)
			TranslateEntity p\camera,0,1,-5
			PointEntity p\camera,p\entity,0
		Case 3:
			EntityParent p\camera,p\entity
			PositionEntity p\camera,0,.25,0
			RotateEntity p\camera,0,0,0
		Case 4:
			EntityParent p\camera,0
			PointEntity p\camera,p\entity,0
		End Select
	EndIf
End Function

Function LoadAirPlane(file$,tex_file$)
	pivot=CreatePivot()
	plane=LoadMesh(file$,pivot)
	tex = LoadTexture(tex_file$)
  EntityTexture(plane, tex)
	ScaleMesh(plane,.125,.15,.125)	;make it more spherical!
	RotateEntity(plane,0,270,-10)	;pakeliame lëktuvo prieká kampu 10, apsukame  aplinkui kampu 180  
	EntityRadius pivot,1
	EntityType pivot,1
	HideEntity pivot
	Return pivot
End Function

Function CreatePlayer.Player(plane, vp_x, vp_y, vp_w, vp_h, ctrl_mode)
	p.Player=New Player
	p\ctrl_mode=ctrl_mode
	p\cam_mode=1
	x#=ctrl_mode*10
	z#=ctrl_mode*10-2500
	p\entity=CopyEntity(plane)
	PositionEntity p\entity,x,TerrainY( terr,x,0,z )+1,z
	RotateEntity p\entity,0,180,0
	ResetEntity p\entity
	p\camera=CreateCamera( p\entity )
	PositionEntity p\camera,0,3,-10
	CameraViewport p\camera,vp_x,vp_y,vp_w,vp_h
	CameraClsColor p\camera,0,192,255
	CameraFogColor p\camera,0,192,255
	CameraFogRange p\camera,1000,3000
	CameraRange p\camera,1,3000
	If use_fog Then CameraFogMode p\camera,1

	;LEKTUVO PARAMETRU NUSTATYMAS
	p\max_speed = 700
	Return p
End Function

Function CreateScene()
	;setup lighting
	l=CreateLight()
	RotateEntity l,45,45,0
	AmbientLight 32,32,32
	
	;Load terrain
	terr=LoadTerrain( "hmap_1024.bmp" )
	ScaleEntity terr,20,800,20
	PositionEntity terr,-20*512,0,-20*512
	EntityFX terr,1
	EntityType terr,10

	;apply textures to terrain	
	tex1=LoadTexture( "coolgrass2.bmp",1 )
	ScaleTexture tex1,10,10
	tex2=LoadTexture( "lmap_256.bmp" )
	ScaleTexture tex2,TerrainSize(terr),TerrainSize(terr)
	EntityTexture terr,tex1,0,0
	EntityTexture terr,tex2,0,1
	
	;and ground plane
	plane=CreatePlane()
	ScaleEntity plane,20,1,20
	PositionEntity plane,-20*512,0,-20*512
	EntityTexture plane,tex1,0,0
	EntityOrder plane,3
	EntityFX plane,1
	EntityType plane,10
	
	;create cloud planes
	tex=LoadTexture("cloud_2.bmp",3 )
	ScaleTexture tex,1000,1000
	p=CreatePlane()
	EntityTexture p,tex
	EntityFX p,1
	PositionEntity p,0,450,0
	p=CopyEntity( p )
	RotateEntity p,0,0,180

  ;create coins
	For i = 1 To 1000
		c = CopyEntity(coin)
		PositionEntity(coin, Rand(-512*20, 512*20) , Rand(50, 300), Rand(-512*20, 512*20))
	Next

	;create trees
			tx#=10
			tz#=-2200
			ty#=TerrainY(terr, tx, 1, tz)
		t = CopyEntity(tree_sprite)
		PositionEntity(t, tx, ty, tz)
		ScaleSprite(t, 20, 40)
		EntityType(t, 5)
		EntityRadius(t, 30, 80)
		
End Function

Function Setup()
	coin = LoadMesh("models/rc-coin.3DS")
	tex_coin = LoadTexture("models/rc-coin.jpg",7)
	EntityTexture(coin, tex_coin)
	EntityColor(coin, 155, 0, 0)
	ScaleEntity(coin, 0.75, 0.75, 0.75)
	EntityRadius (coin, 50)
	EntityType(coin, type_item)

	tree_sprite = LoadSprite("sprites/tree.bmp", 7)
	HandleSprite tree_sprite,0,-1
	;ScaleSprite tree_sprite, 2, 4
	SpriteViewMode tree_sprite, 3
	EntityAutoFade tree_sprite, 120, 150

	spark_sprite = LoadSprite( "sprites/bigspark.bmp" )
	HideEntity spark_sprite

	bull_sprite = LoadSprite( "sprites/bluspark.bmp" )
	ScaleSprite bull_sprite,3,3
	EntityRadius bull_sprite,1.5
	EntityType bull_sprite,3
	HideEntity bull_sprite

End Function

Function CreateBullet.Bullet( p.Player )
	b.Bullet = New Bullet
	b\sprite = CopyEntity(bull_sprite, p\entity)
	TranslateEntity(b\sprite, 0, 0.5, .25)
	EntityParent(b\sprite, 0)
	EmitSound(shoot, b\sprite)
	EmitSound(boom, b\sprite)

	
	Return b
End Function

Function UpdateBullet(b.Bullet)

	If EntityCollided(b\sprite, type_ground)
	CreateSpark(b)
	FreeEntity b\sprite
	Delete b
	Return
EndIf 
	
	MoveEntity b\sprite,0,0, (speed/100 + 10)

	;DETECT COLLISIONS:
	item = EntityCollided(b\sprite, type_item)
	If(item) Then
		score = score + 10
		CreateSpark(b)
		FreeEntity b\sprite
		HideEntity(item)
		Delete b
		Return
	EndIf
	RotateSprite b\sprite,b\rot	
End Function

Function CreateSpark.Spark(b.Bullet)
	s.Spark=New Spark
	s\alpha=-90
	s\sprite=CopyEntity(spark_sprite,b\sprite)
	EntityParent s\sprite,0
	Return s
End Function

Function UpdateSpark(s.Spark)
	If s\alpha < 270
		sz#=Sin(s\alpha)*5+55
		ScaleSprite s\sprite,sz,sz
		RotateSprite s\sprite,Rnd(360)
		s\alpha=s\alpha+15
	Else
		FreeEntity s\sprite
		Delete s
	EndIf
End Function
Function GamerOver()
	FreeSound touch
	ClsColor(0, 0, 0)
	While Not KeyHit(1)
	Cls
		image = LoadImage("gameover.jpg")
		DrawBlock(image, 100, 200)
		Color (255 ,0 ,0)
		font1 = LoadFont("Verdana", 26, True, False, False)
		SetFont font1
		Text(350, 350,"SCORE: " + score +"10000000")
		Flip
	Wend 

End Function 