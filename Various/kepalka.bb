Graphics3D (1280,1024,32,1)
SetBuffer (BackBuffer())

cam = CreateCamera()
CameraClsColor (cam, 100, 0, 50)
PositionEntity (cam, 0, 0, -4)
light = CreateLight()
RotateEntity light,90,0,0


;OBJEKTAI:

kubas = CreateCube()
	PositionEntity(kubas, 0, 0, 5)
	EntityColor(kubas, 225, 225, 225)
	EntityAlpha(kubas, 1.5)

ball = CreateSphere()
	PositionEntity(ball, 1, 1, 2)
	EntityColor(ball, 41, 50, 100)
	EntityAlpha(ball, 5)

cylinder = CreateCylinder()
	PositionEntity(cylinder, 3, 2, 5)
	EntityColor(cylinder, 125, 0, 50)
	
cone = CreateCone()
	PositionEntity(cone, 4, 2, 1)
	EntityColor(cone, 125, 150, 40)




	
i=0
	While(Not KeyDown(1))
	If KeyDown( 203 )= True Then MoveEntity cone,-0.1,0,0 
	If KeyDown( 205 )= True Then MoveEntity cone,0.1,0,0 
	If KeyDown( 208 )= True Then MoveEntity cone,0,-0.1,0 
	If KeyDown( 200 )= True Then MoveEntity cone,0,0.1,0 
	If KeyDown( 44 )= True Then MoveEntity cone,0,0,-0.1 
	If KeyDown( 30 )= True Then MoveEntity cone,0,0,0.1 

	i=i+1
	x# = Sin(i)
	If i>360 Then i=1
	TurnEntity(kubas, 1, 3, 0)
	ScaleEntity(kubas, x, x, x)
	TurnEntity(ball, 1, 0, 10)
	ScaleEntity(ball, x, x, x)
	TurnEntity(cylinder, 5, 0, 3)
	ScaleEntity(cylinder, x, x, x)
	TurnEntity(cone, 20, 0, 3)
	ScaleEntity(cone, x, x, x)


	

	
	RenderWorld()
	Flip()
	Wend 