Graphics3D (1280,800,16,1)
SetBuffer (BackBuffer())  

camera = CreateCamera()
CameraClsColor (camera, 0, 225, 225)
PositionEntity(camera, 0, 0, -10)
light = CreateLight()

cube = CreateCube()
PositionEntity(cube, 5, 0, 0)
	EntityColor(cube, 140, 0, 150)
	
cylinder = CreateCylinder()
PositionEntity(cylinder, 2, 2, 2)
	EntityColor(cylinder, 25, 40, 0)
	EntityAlpha(cylinder, 0.5)

cone = CreateCone()
PositionEntity(cone, 10, 2, 2)
EntityColor(cone, 60, 0, 20)

	i=0
While(Not KeyDown(1))
	i=i+1
	If i>360 Then i=1
	x#=Sin(i)
	ScaleEntity(cube, Sin(i), Sin(i), Sin(i))
	TurnEntity (cube, 2, 5, 0)
	ScaleEntity(cylinder, Sin(i), Sin(i), Sin(i))
	TurnEntity (cylinder, 4, 0, 2)
	ScaleEntity(cone, x , x, x)
	TurnEntity (cone, 10, 2, 2)
	
	RenderWorld()
	Flip()
Wend 