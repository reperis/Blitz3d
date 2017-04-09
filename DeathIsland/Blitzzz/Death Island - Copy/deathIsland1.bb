Global dWidth  = 1440
Global dHeight = 900

Graphics3D(dWidth, dHeight, 16)
SetBuffer BackBuffer ()

AmbientLight 0, 0, 0

Global lensDiameter = 25
Include "incs/lensIncs.bb"

Global aim_sprite, bull_sprite, spark_sprite
Global terrain, water, dome, grass, seabed, clouds, flash
Global wind, sea, rain, run, wade, thunder1, thunder2
Global cam, camera
Global dest_xang#, dest_yang#, xang#, yang#
Global steps# = 1.5
Global horror, horrorAlpha#, disclaimer$
Global picked#

Global laser
Global ruger
Global gunswich = -1

Global dropx# = 0
Global dropy# = 0
Global dropx2# = 0
Global dropy2# = 0
Global sky# = 0
Global startlite# = 0
Global foglite# = 0
Global sunlite# = 0

Global points 
Global health = 10
Global kulkos = 100

Global runChannel = PlaySound (run)
Global wadeChannel = PlaySound (wade)

Type Spark
	Field alpha#, sprite
End Type

Type Bullet
	Field rot#, sprite
End Type

Type dog
	Field ID
End Type

Type Timer
	Field start
	Field timeOut
End Type

; Collisions...
Const ENTITY_TERRAIN = 1
Const ENTITY_CAM	 = 2
Const ENTITY_OBJECT  = 3 ; Tower and Barrel
Const ENTITY_BEAST = 4
Const ENTITY_BULLET = 5

Collisions ENTITY_CAM, ENTITY_TERRAIN, 2, 2
Collisions ENTITY_CAM, ENTITY_OBJECT, 2, 1
Collisions ENTITY_CAM, ENTITY_BEAST, 2, 1
Collisions ENTITY_BEAST, ENTITY_CAM, 2, 1
Collisions ENTITY_BULLET, ENTITY_BEAST, 2, 1
Collisions ENTITY_BULLET, ENTITY_TERRAIN, 2, 2
Collisions ENTITY_BULLET, ENTITY_OBJECT, 2, 1


Function LoadMusic()
	wind 	 = LoadSound ("snd/seawind2.wav")
	sea  	 = LoadSound ("snd/wavdance.wav")
	rain 	 = LoadSound ("snd/rainroof.wav")
	thunder1 = LoadSound ("snd/thunder.wav")
	thunder2 = LoadSound ("snd/txstorm.wav")
	run	 	 = LoadSound ("snd/gravel.wav")
	wade	 = LoadSound ("snd/water2.wav")

	SoundVolume wind, 0.25
	SoundVolume sea, 0.65
	SoundVolume rain, 0.75
	SoundVolume run, 0
	SoundVolume wade, 0

	LoopSound wind
	LoopSound sea
	LoopSound rain
	LoopSound run
	LoopSound wade

	PlaySound wind
	PlaySound sea
	PlaySound rain
End Function

Function CenterPivot (model)
	FitMesh model, 0, 0, 0, MeshWidth (model), MeshHeight (model), MeshDepth (model)
	modelPivot = CreatePivot ()
	PositionEntity modelPivot, MeshWidth (model) / 2, MeshHeight (model) / 2, MeshDepth (model) / 2
	EntityParent model, modelPivot
	Return modelPivot
End Function

Function Setup()
	;taikinukas
	aim_sprite=LoadImage("gfx/aim.bmp")
	MidHandle aim_sprite
	
	spark_sprite = LoadSprite( "gfx/images.jpg" )
	HideEntity spark_sprite
	bull_sprite = LoadSprite( "gfx/bigspark2.bmp" )
	ScaleSprite bull_sprite,3,3
	EntityRadius bull_sprite,1.5
	HideEntity bull_sprite
	EntityType bull_sprite, ENTITY_BULLET
End Function

Function Camera()
	cam = CreatePivot()
	EntityRadius cam, 2
	
	camera = CreateCamera(cam)
	
	CameraViewport camera, 0, 0, dWidth, dHeight
	CameraFogMode camera, 1
	CameraFogRange camera, 1, 1600; * scaler
	CameraFogColor camera, 0, 0, 0;75, 75, 75
	CameraRange camera, 1, 1600 * 2
	PositionEntity cam, 2340, 0, 2390 ; 1394, 0, 4660 ;1298, 0, 4653 ; 3750, 45, 1370
	EntityType cam, ENTITY_CAM
	End Function

Function CreateScene()
	terrain = LoadTerrain ("gfx/height.bmp")
	ScaleEntity terrain, 5, 150, 5
	TerrainShading terrain, True
	TerrainDetail terrain, 2500, True
	EntityType terrain, ENTITY_TERRAIN

	grass = LoadTexture ("gfx/greygrass.bmp")
	ScaleTexture grass, 20, 20
	EntityTexture terrain, grass, 0, 1
	MoveEntity terrain, 0, -4, 0
	EntityType terrain, ENTITY_TERRAIN

	seabed = CreatePlane ()
	EntityTexture seabed, grass
	MoveEntity seabed, 0, -3.9, 0

	water = CreatePlane ()
	h20 = LoadTexture ("gfx/greywater.bmp")
	EntityAlpha water, 0.75
	PositionEntity water, 0, 2.5, 0
	ScaleTexture h20, 200, 200
	EntityTexture water, h20
	EntityColor water, 64, 64, 64
	EntityShininess water, 0.05

	dome = CreateSphere (12)

	clouds = LoadTexture ("gfx/realsky.bmp")
	ScaleEntity dome, 1600 * 2, 1600 * 2, 1600 * 2
	EntityTexture dome, clouds
	ScaleTexture clouds, 0.25, 0.25
	EntityOrder dome, 1
	FlipMesh dome
	EntityAlpha dome, 0.25
	EntityFX dome, 8

	flash = CreateLight ()
	LightColor flash, 0, 0, 0
	PositionEntity flash, 1900, 100, 0
	RotateEntity flash, 90, 0, 0

	b747Model = LoadMesh ("msh/747.x")
	EntityShininess b747Model, 0.1
	b747 = CenterPivot (b747Model)
	ScaleEntity b747, 600, 600, 600 ; 500
	PositionEntity b747, 3750, 400, 1300
	RotateEntity b747, 3, 45, -1
	EntityType b747Model, ENTITY_OBJECT

	tower = LoadMesh ("msh/middletower.3ds")
	;EntityShininess tower, 0.1
	;ScaleEntity tower, 600, 600, 600
	PositionEntity tower, 3100, 160, 2800
	RotateEntity tower, 3, 45, -1
	EntityType tower ,ENTITY_OBJECT

	oildrum = LoadMesh ("msh/oildrum.3ds")
	PositionEntity oildrum, 3200, 130, 2660
	RotateEntity oildrum, 3, 45, -1
	EntityType oildrum, ENTITY_OBJECT
	
	laser=LoadMesh("msh/lazer/lazer.3ds",cam);gun creation
	LoadTexture laser,"msh/lazer/lazer.jpeg"
	PositionEntity laser, 3, 0, 5

	ruger=LoadMesh("msh/pistol/pistol.3ds",cam);gun creation
	PositionEntity ruger, 6, 0, 10
	RotateEntity ruger, -90, 90, 0
	EntityColor ruger, 255, 0, 0
	ScaleEntity ruger, 0.2, 0.2, 0.2
	HideEntity  ruger
	End Function

Function SetTimer.Timer (timeOut)
	t.Timer = New Timer
	t\start   = MilliSecs ()
	t\timeOut = t\start + timeOut
	Return t
End Function

Function TimeOut (test.Timer)
	If test <> Null
		If test\timeOut < MilliSecs ()
			Delete test
			Return 1
		EndIf
	EndIf
End Function

Function CurveValue#(current#,destination#,curve)
	current#=current#+((destination#-current#)/curve)
	Return current#
End Function

Function UpdatePlayer()
		PositionEntity cam, EntityX (cam), TerrainY (terrain, EntityX (cam), EntityY (cam), EntityZ (cam)) + 25, EntityZ (cam)
		mxs = MouseXSpeed()
		mys = MouseYSpeed()

		dest_xang# = dest_xang + mys
		dest_yang# = dest_yang - mxs
	
		xang# = CurveValue (xang, dest_xang, 5)
		yang# = CurveValue (yang, dest_yang, 5)
	
		RotateEntity cam, xang, yang, 0
		
		MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
		
		running# = 0

		If (MouseHit(2))
			If(gunswich = 0)
				HideEntity(laser)
				ShowEntity(ruger)
				gunswich = 1
			Else
				HideEntity(ruger)
				ShowEntity(laser)
				gunswich = 0
			EndIf
		EndIf

		If (MouseHit(1) And (kulkos > 0))
			CreateBullet(laser)
			kulkos = kulkos - 1
		ElseIf KeyHit(19)
			kulkos = 100
		EndIf
		
		If KeyDown (17) 							;move forward
			MoveEntity cam, 0, 0, steps
			running = Rnd (0.35, 0.75)
		EndIf
	
		If KeyDown (31)								;move backwards
			MoveEntity cam, 0, 0, -steps
			running = Rnd (0.55, 0.95)
		EndIf
		If KeyDown (30)								;move left
			MoveEntity cam, -steps, 0, 0
			running = Rnd (0.55, 0.95)
		EndIf
		If KeyDown (32)								;move right
			MoveEntity cam, steps, 0, 0
			running = Rnd (0.55, 0.95)
		EndIf
	
		If running > 0
			ChannelPitch runChannel, Rnd (8000, 14000)
			ChannelPitch wadeChannel, Rnd (7000, 9000)
		EndIf
		
		wading# = 0
		
		If running
			If EntityY (cam) < EntityY (water) + 24
				wading = Rnd (0.5, 1)
				running = 0
			EndIf
		EndIf
		
		ChannelVolume wadeChannel, wading
		ChannelVolume runChannel, running
	
		PositionEntity dome, EntityX (cam), EntityY (cam), EntityZ (cam)
		TurnEntity dome, 0.005, 0.025, 0.005

		enemy = EntityCollided(cam, ENTITY_BEAST)
		If (enemy) Then
		health = health -10
		EndIf

End Function

Function LoadDogs(file$, file2$)
	model=LoadAnimMesh(file$)
	tex0 = LoadTexture(file2$)
	EntityTexture(model, tex0)
	ExtractAnimSeq model,1,25
	ExtractAnimSeq model,382, 400
	RotateEntity(model, 0,180,0)
	;ScaleEntity model,.2,.2,.2
	For c=1 To 50
		Pack.dog = New dog
		Pack\ID = CopyEntity(model)
		x=Rand(-2000,2000):z=Rand(-2000,2000)
		PositionEntity Pack\ID, x,TerrainY(terrain,x,0,z),z
		Animate Pack\ID,2,.3,1
		;Animate Pack\ID,1,.3,1
		EntityRadius Pack\ID,132
		EntityAutoFade Pack\ID,500,1000
		EntityType Pack\ID,ENTITY_BEAST
	Next
	FreeEntity model
End Function

Function MoveDogs(obj)
	For moveall.dog = Each dog
		If Rand(1,100)=100 Then PointEntity moveall\ID,obj TurnEntity moveall\ID,0,180,0
		MoveEntity moveall\ID,0,0,-.5
		ex#=EntityX(moveall\ID):ez#=EntityZ(moveall\ID)
		PositionEntity moveall\ID,ex,TerrainY(terrain,ex,0,ez ),ez
	Next
End Function

Function CreateBullet.Bullet(obj)
	b.Bullet = New Bullet
	b\sprite = CopyEntity(bull_sprite, obj)
	TranslateEntity(b\sprite, 0, 0.5, 0.25)
	EntityParent(b\sprite, 0)
	Return b
End Function

Function UpdateBullet(b.Bullet)
	If EntityCollided(b\sprite, ENTITY_TERRAIN)
		CreateSpark(b)
		FreeEntity b\sprite
		Delete b
		Return
		EndIf
		If EntityCollided(b\sprite, ENTITY_OBJECT)
		CreateSpark(b)
		FreeEntity b\sprite
		Delete b
		Return
		EndIf
	MoveEntity b\sprite,0,0, (speed/100 + 10)
	;RotateSprite b\sprite,b\rot
	;DETECT COLLISIONS:
	nusauta = EntityCollided(b\sprite, ENTITY_BEAST)
	If(nusauta) Then
		FreeEntity b\sprite
		Animate nusauta, 0
		Animate nusauta,1,0.3,2
		;HideEntity(nusauta)
		Delete b
		Return
	EndIf
	
	If EntityY(b\sprite) > 500
			FreeEntity b\sprite
		Delete b
 	EndIf

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

Function UpdateGame()
	For b.Bullet=Each Bullet
		UpdateBullet( b )
	Next
	For s.Spark=Each Spark
		UpdateSpark( s )
	Next
End Function

Function Titles()
; Titles...
horror = LoadSprite ("gfx/di.bmp", 48, camera)
ScaleSprite horror, 9, 5
PositionEntity horror, 0, 0, 10
horrorAlpha = 0
EntityAlpha horror, horrorAlpha
showHorror = 1

HidePointer

disclaimer$ = "Run it at night or you won't see a thing :)"
SetFont LoadFont ("arial")
End Function
Function pinge()
pinge = EntityCollided(ENTITY_BULLET, ENTITY_BEAST)
If (pinge) Then
points = points +5
End Function
Function GameOver()
ClsColor(0, 0, 0)
While Not KeyHit(1)
	Cls
	GameOver = LoadImage("asesueldudievas.jpg")
	DrawBlock(GameOver, 100, 200)
	Color (255 ,0 ,0)
	Flip
Wend
End Function
Function GameWin()
If points = 50 Then
LoadImage("images.jpg")
End Function

; ---------------------------------------------------------------
; Main loop...
; ---------------------------------------------------------------
FPS = 50
period = 1000 / FPS
time = MilliSecs () - period

Camera()
LoadMusic()
CreateScene()
Setup()
LoadDogs("beast.b3d", "beast3.jpg")

; ---------------------------------------------------------------
; Lens stuff...
; ---------------------------------------------------------------
diameter = lensDiameter
magnification = 5
CreateLens(lensDiameter, magnification)

Titles()

While (Not KeyDown (1)) And (health>0)
	If startlite < 40
		startlite = startlite + 0.5
		AmbientLight startlite, startlite, startlite
	EndIf

	If makesun = 0
		sun = CreateLight ()
		LightColor sun, 0, 0, 0
		PositionEntity sun, 3000, 500, 3000
		RotateEntity sun, 45, 0, 0
		makesun = 1
	Else
	; Titles... requesting the services of Major Hack... Major Hack, you're needed for some titles...
		If Not noMoreHorror
			EntityAlpha horror, horrorAlpha
			If Not reduce
				If horrorAlpha <= 1
					horrorAlpha = horrorAlpha + 0.05
				Else
					reduce = 1
				EndIf
			Else
				If horrorAlpha => 0
					horrorAlpha = horrorAlpha - 0.005
				EndIf
			EndIf	
			If horrorAlpha <= 0
				FreeEntity horror
				noMoreHorror = 1
			EndIf
		EndIf	
	EndIf
	
	If sunlite < 70
		sunlite = sunlite + 0.5
		If sun
			LightColor sun, sunlite, sunlite, sunlite
		EndIf
	EndIf

	If foglite < 20
		foglite = foglite + 0.5
		CameraFogColor camera, foglite, foglite, foglite
		CameraClsColor camera, foglite, foglite, foglite
	EndIf

	Repeat
		elapsed = MilliSecs () - time
	Until elapsed

	ticks = elapsed / period
	
	tween# = Float (elapsed Mod period) / Float (period)
	
	For framelimit = 1 To ticks
	
		If framelimit = ticks Then CaptureWorld
		time = time + period

		MoveDogs(cam)
		UpdateGame()
		UpdatePlayer()
		UpdateWorld
		
		; Mark's code, hacked cluelessly...
		PositionEntity water, Sin (time * 0.01) * 10, (EntityY (water) + (Sin (time * 0.05) * 0.2) * 0.25), Cos (time * 0.02) * 10

		If (Rnd (1000) > 998.8) Or (KeyDown (28))
			startlite = 0
			sunlite = 40;255;10
			sky = 255
			foglite = 255
			thunderGo = 1
			thunderTimer.Timer = SetTimer (Rnd (500, 1500))
		EndIf

		If thunderGo
			If TimeOut (thunderTimer)
				If Rnd (0, 2) > 1
					thunder = thunder1
				Else thunder = thunder2
				EndIf
				SoundPitch thunder, Rnd (9000, 14000)
				PlaySound thunder
				thunderGo = 0
				Delete thunderTimer
			EndIf
		EndIf
				
		If sky > 10
			sky = sky - Rnd (5, 20)
		EndIf
		LightColor flash, sky, sky, sky

		If foglite > 20
			foglite = foglite - 10
			If foglite < 20 Then foglite = 20
		EndIf
		CameraClsColor camera, foglite, foglite, foglite					
;		CameraFogColor camera, foglite, foglite, foglite					

	Next
	
	; W is for Wireframe...
	If KeyHit (15)
		w = 1 - w
		WireFrame w
	EndIf
	
	RenderWorld tween

	If Not nomorehorror
		Text (GraphicsWidth () / 2) - (StringWidth (disclaimer$) / 2), GraphicsHeight () - (StringHeight (disclaimer$) * 2), disclaimer$
	EndIf
	
; Raindrops on lens...
	If dropy <= GraphicsHeight () - diameter - 2
		DrawLens (dropx, dropy, diameter)
		dropy = dropy + 2
	Else
		dropx = Rnd (0, GraphicsWidth () - diameter)
		dropy = 0
	EndIf

	If dropy2 <= GraphicsHeight () - diameter - 7
		DrawLens (dropx2, dropy2, diameter)
		dropy2 = dropy2 + 7
	Else
		dropx2 = Rnd (0, GraphicsWidth () - diameter)
		dropy2 = 0
	EndIf
	
	DrawImage (aim_sprite,(dWidth/2),(dHeight/2))

	Text 0, 0, "x: "+ EntityX(Cam)
	Text 0, 20, "y: "+ EntityY(Cam)
	Text 0, 40, "z: "+ EntityZ(Cam)
	Text (0, 60, "Bullets: " + kulkos)
	Text (300, 0, "health: " + health)
	Text (0, 80, 0, "points" + points)
	Flip
	
Wend
	GameOver()
End