Graphics3D(1024, 768, 16, 1)
SetBuffer(BackBuffer())
Const type_player = 1, type_enemy = 2, type_item = 3, type_ground = 4

Type Player
	Field entity
	Field camera
	Field x, y, z
	Field score, health
End Type


CreateScene()
p1.Player = CreatePlayer(0, 0, 0)

Collisions(type_player, type_enemy, 2, 1)
Collisions(type_player, type_item, 2, 2)
Collisions(type_player, type_ground, 2, 2)
While(Not KeyDown(1))
  If(p1\health > 0) UpdatePlayer()
	UpdateWorld()
	RenderWorld()
	PrintStats()
	Flip()
Wend
Function CreateScene()
  light = CreateLight()
	;Load terrain
	ground = LoadTerrain("./environment/hmap_1024.bmp")
	ScaleEntity(ground, 20, 800, 20)
	PositionEntity(ground, -20*(1024/2), -1, -20*(1024/2) - 1200)
	EntityFX(ground, 1)
	EntityType(ground, type_ground)

	;Apply textures
	tex1 = LoadTexture("./environment/coolgrass2.bmp")
	ScaleTexture(tex1, 10, 10)
	EntityTexture(ground, tex1, 0, 0)
	tex2 = LoadTexture("./environment/terrain-1.jpg")
	ScaleTexture(tex2, TerrainSize(ground), TerrainSize(ground))
	EntityTexture(ground, tex2, 0, 1)
	
	For i = 0 To 10
	  For j = 0 To 10
	  	For k = 0 To 10
				c = LoadMesh ("./models/star.3ds")
					ScaleMesh(c, 0.2, 0.2, 0.2)
					 ;m = LoadMesh("./city.3ds")
					 ;ScaleMesh(m, 0.009, 0.009, 0.009)
					 
						
        t = Rand(1, 10)
				If(t = 1) Then
					EntityColor(c, 150, 0, 0)
					EntityType(c, type_enemy)
				Else
					EntityColor(c, 255, 255, 255)
					EntityType(c, type_item)					
				EndIf
				PositionEntity(c,Rnd(3,100), Rnd(0,190), Rnd(0,250))
				
			Next
		Next		
	Next 
End Function
Function CreatePlayer.Player(x, y, z)
	p.Player = New Player
	p\entity = LoadMesh("./models/fighter1.3ds")
	ScaleMesh(p\entity, 0.125, 0.15, 0.125)
	RotateMesh(p\entity, 0, 270, -10)
	tex = LoadTexture("./models/plane1.jpg")
	EntityTexture(p\entity, tex)
	EntityType(p\entity, type_player)
	PositionEntity(p\entity, x, y, z)
  p\camera = CreateCamera(p\entity)
	PositionEntity(p\camera, 0, 0, -8)
	p\score = 0
	p\health = 100
	Return p
End Function
Function UpdatePlayer()
For p.Player = Each Player
	;KEYBOARD CONTROL:
  If(KeyDown(200)) MoveEntity(p\entity,  0, 0,  0.5); FORWARDS
	If(KeyDown(208)) MoveEntity(p\entity,  0, 0, -0.5); BACKWARDS	
	If(KeyDown(203)) MoveEntity(p\entity, -0.5, 0,  0); STAFE LEFT
	If(KeyDown(205)) MoveEntity(p\entity,  0.5, 0,  0); STAFE RIGHT
	;MOUSE CONTROL:
  TurnEntity(p\entity, MouseYSpeed(), 0, 0)				 ; LOOK UP-DOWN
  TurnEntity(p\entity, 0, - MouseXSpeed(), 0, True) ; LOOK LEFT-RIGHT
	MoveMouse(GraphicsWidth() / 2, GraphicsHeight() / 2)
	;DETECT COLLISIONS:
	item = EntityCollided(p\entity, type_item)
	If(item) Then
		p\score = p\score + 1
		HideEntity(item)
	EndIf
	enemy = EntityCollided(p\entity, type_enemy)	
	If(enemy) Then
		p\score = p\score - 10
		p\health = p\health - 10
		HideEntity(enemy)		
	EndIf
	ManagePlayerHealth()
Next
End Function
Function ManagePlayerHealth()
For p.Player = Each Player
	If(p\health > 0) Then
		EntityAlpha(p\entity, p\health / 100.0)
  Else
		;GAME OVER
	EndIf
Next
End Function 
Function PrintStats()
For p.Player = Each Player
	Text(0, 0,  "SCORE:  " + p\score)
	Text(0, 10, "HEALTH: " + p\health)	
	;Text(0, 0, "(" + MouseX() + ", " + MouseY() + ", " + MouseZ() + ")")
	;Text(0, 10, "(" + MouseXSpeed() + ", " + MouseYSpeed() + ", " + MouseZSpeed() + ")")
Next
End Function
