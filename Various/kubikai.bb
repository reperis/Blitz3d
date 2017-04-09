Graphics3D(1280, 1024, 16)
SetBuffer (BackBuffer())

camera = CreateCamera()
CameraClsColor(camera, 225, 225, 225)
light = CreateLight()

PositionEntity(camera, 0, 0,-30)

Const cubecount = 10
Dim cube (cubecount)
For i = 0 To cubecount-1
	cube(i) = CreateSphere()
	PositionEntity (cube(i), 5*i - 0*i, 0, 0)
	MoveEntity(cube(i), -5*cubecount/2, 0, 0)
	EntityAlpha(cube(i), i*(1.0/cubecount)+1.0/cubecount)
	;EntityColor(cube(i), 225, 225, 0)
	kepalas = LoadTexture(".//zeme.jpg")
	EntityTexture cube(i), kepalas
	
Next 
ifpause = False
While (Not KeyDown(1))
	If Not KeyDown(57)Then ifpause = True Else ifpause = False 
	If ifpause = False Then
	For  i = 0 To cubecount-1
	TurnEntity(cube(i), 0, 3, 0)
	If KeyDown(17) = True Then MoveEntity(camera, 0, 0, 1)
	If KeyDown(31) = True Then MoveEntity(camera, 0, 0, 1)
	If KeyDown(30) = True Then MoveEntity(camera, 0, 1, 0)
	If KeyDown(32) = True Then MoveEntity(camera, 0, 1, 0)
		Next
	EndIf
	
	RenderWorld()
	Flip()
Wend 