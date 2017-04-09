Graphics3D(1280, 800, 32, 1)
SetBuffer(BackBuffer())

Const type_player = 1, type_enemy = 2, type_item = 3
Type player
  Field entity
  Field camera
  Field x, y, z
  Field health%, score%
End Type
CreateScene()
player1.player = createplayer(0, 0, 0)

Collisions(type_player, type_item, 2, 2)
Collisions(type_player, type_enemy, 2, 1)
Collisions(type_player, type_ground, 2, 3)

While(Not KeyDown(1))
	For p.Player = Each Player
  	If(p\health > 0) UpdatePlayer()
		UpdateWorld()
	Next
	RenderWorld()
	PrintStats()			
	Flip() 															
Wend

Function CreateScene()
	light = CreateLight()
	ground = LoadTerrain("./environment/hmap_1024.bmp")
	ScaleEntity(ground, 20, 800, 20)
	PositionEntity(ground, -20*512, -1, -2*512 - 1200)
	EntityType(ground, type_ground)

	tex1 = LoadTexture("./environment/coolgrass2.bmp",1)
	EntityTexture ground, tex1
	ScaleTexture(tex1, 10, 10)
	tex48 = LoadTexture("./environment/terrain-1.jpg")
	EntityTexture ground, tex48
	ScaleTexture(tex48, TerrainSize(ground), TerrainSize(ground))
	For i = 0 To 10
	  For j = 0 To 10
	  	For k = 0 To 10
				c = CreateCube()
        t = Rand(1, 10)
				If(t = 1) Then
					EntityColor(c, 150, 0, 0)
					EntityType(c, type_enemy)
				Else
					EntityColor(c, 255, 255, 255)
					EntityType(c, type_item)					
				EndIf
				PositionEntity(c, 10 * i, 10 * j, 10 * k)
			Next
		Next		
	Next 
End Function
Function CreatePlayer.player(x, y, z)
 p.player = New player
 p\entity = LoadMesh ("./models/fighter1.3ds")
 tex = LoadTexture ("./models/plane4.jpg")
 something = (".")
 EntityTexture (p\entity, tex)
 RotateMesh(p\entity, 0, 270, -10)
 ScaleMesh(p\entity, 0.1, 0.1, 0.1)
 p\camera = CreateCamera(p\entity)
 EntityType(p\entity, type_player)
 PositionEntity(p\camera, 0, 0, -10)
 p\health = 100
 p\score = 0
 Return p 
 End Function
Function UpdatePlayer()
  For p.player = Each player
  If(KeyDown (200)) MoveEntity(p\entity,  0,  0, 0.5)    ; move forwards
  If(KeyDown (208)) MoveEntity(p\entity,  0, 0, -0.5)    ; move backwards
  If(KeyDown (203)) MoveEntity(p\entity,  -0.5, 0, 0); turn Left
  If(KeyDown (205)) MoveEntity(p\entity,  0.5, 0, 0)  ; turn RIGHT
  TurnEntity(p\entity, 0, - MouseXSpeed(), 0, True)          ; up down
  TurnEntity(p\entity, MouseYSpeed(), 0, 0, False)           ; left right
  MoveMouse(GraphicsWidth()/2, GraphicsHeight()/2)           ; recenter

  ;detect collisions
	item = EntityCollided(p\entity, type_item)
	If(item) Then
		p\score = p\score + 5
		HideEntity(item)
	EndIf
	enemy = EntityCollided(p\entity, type_enemy)	
	If(enemy) Then
		p\score = p\score - 5
		p\health = p\health - 10
		HideEntity(enemy)		
	EndIf
	ManagePlayerHealth()
	Next
End Function
Function PrintStats()
For p.player = Each Player
	Text (0, 0, "Score:" + p\score)
	Text (0, 10, "Health:" + p\health)
	
Next 
  ;Text(0, 0, "(" +MouseX() + ", " + MouseY() + ", " +  MouseZ() + ")")
  ;Text(0, 10, "(" +MouseXSpeed() + ", " + MouseYSpeed() + ", " +  MouseZSpeed() + ")")
End Function
Function ManagePlayerHealth()
	For p.player = Each Player
		If(p\health > 0) Then
			EntityAlpha(p\entity, p\health / 100.0)
		Else
			;Game over
		UpdateWorld()
		RenderWorld()
		EndIf
	Next
End Function