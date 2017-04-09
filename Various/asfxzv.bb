Graphics3D (1024,768,16,1)
SetBuffer (BackBuffer())

cam = CreateCamera()
CameraClsColor (cam, 150, 190, 150)
PositionEntity (cam, 0, 8, -5)
light = CreateLight()
RotateEntity light,90,0,0

;OBJEKTAI:

kubas = CreateCube()
	PositionEntity(kubas, 0, 10, 5)
	EntityColor(kubas, 225, 225, 225)
	EntityAlpha(kubas, 1.5)

ball = CreateSphere()
	PositionEntity(ball, 1, 5, 2)
	EntityColor(ball, 41, 50, 100)
	EntityAlpha(ball, 5)
	RotateEntity(ball, 90, 0, 0)

cylinder = CreateCylinder()
	PositionEntity(cylinder, 3, 10, 5)
	EntityColor(cylinder, 125, 0, 50)
	
cone = CreateCone()
	PositionEntity(cone, 4, 10, 1)
	EntityColor(cone, 125, 150, 40)

sp = LoadSprite("D:\Arturas\Desktop\programavimui\zombie.jpg") 
	PositionEntity (sp, 0, 2, 2)

bb = LoadSprite("D:\Arturas\Desktop\programavimui\gordon.bmp") 
	SpriteViewMode bb,2
	PositionEntity (bb, 4, 2, 2)


god = LoadTexture("D:\Arturas\Desktop\programavimui\sunset.jpg")
	EntityTexture kubas,god

kepal = LoadTexture("D:\Arturas\Desktop\programavimui\images.jpeg")
	EntityTexture ball,kepal

	
	;GRINDYS:

plane = CreatePlane()
	PositionEntity(plane, 1, 1, 1)
	EntityColor(plane, 0, 225, 50)

grass = LoadTexture ("D:\Arturas\Desktop\programavimui\chorme-2.bmp")
	EntityTexture plane,grass
	EntityColor (plane, 0, 225, 0)

i=0
	While(Not KeyDown(1))

		i=i+1
	x# = Sin(i)
	If i>360 Then i=1
	
	TurnEntity(kubas, 1, 3, 0)
	ScaleEntity(kubas, x, x, x)
	TurnEntity(ball, 0, 5, 0)
	ScaleEntity(ball, x, x, x)
	TurnEntity(cylinder, 5, 0, 3)
	ScaleEntity(cylinder, x, x, x)
	TurnEntity(cone, 2, 0, 3)
	ScaleEntity(cone, x, x, x)
	If KeyDown( 203 )= True Then MoveEntity cam,-0.1,0,0 
	If KeyDown( 205 )= True Then MoveEntity cam,0.1,0,0 
	If KeyDown( 208 )= True Then MoveEntity cam,0,-0.1,0 
	If KeyDown( 200 )= True Then MoveEntity cam,0,0.1,0 
	If KeyDown( 44 )= True Then MoveEntity cam,0,0,-0.1 
	If KeyDown( 45 )= True Then MoveEntity cam,0,0,0.1
	If KeyDown( 30 )= True Then MoveEntity sp,-1,0,0 
	If KeyDown( 32 )= True Then MoveEntity sp,1,0,0 
	If KeyDown( 31 )= True Then MoveEntity sp,0,0,-1 
	If KeyDown( 17 )= True Then MoveEntity sp,0,0,1
	If KeyDown ( 72 ) = True Then MoveEntity bb,0,0,100
	If KeyDown ( 76 ) = True Then MoveEntity bb,0,0,-50 
	If KeyDown ( 75 ) = True Then MoveEntity bb,-50,0,0
	If KeyDown ( 77 ) = True Then MoveEntity bb,50,0,0
	
	RenderWorld()
	Flip()
	Wend 