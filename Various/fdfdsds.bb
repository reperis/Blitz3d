Graphics3D(1024, 768, 16, 1)
SetBuffer(BackBuffer())

cam = CreateCamera()
light = CreateLight()

PositionEntity(cam, 0, 0, -200)
CameraClsColor(cam, 255, 255, 255)
texSun = LoadTexture("./chorme.bmp")


Const cubeCount = 100
Dim cube(cubeCount)
For i = 0 To cubeCount-1
cube(i) = CreateSphere()
EntityTexture(cube(i), texSun)
PositionEntity(cube(i), 5*i, 0, 0)
MoveEntity(cube(i), -5*cubeCount/2, 0, 0)
EntityAlpha(cube(i), i*(1.0/cubeCount) + 1.0/cubeCount)
Next
 Rand(cube(i), Rand 1 To 10)
 Rand cube(i)
While(Not KeyDown(1))
		If(Not KeyDown(57)) Then
			For i = 0 To cubeCount-1
				TurnEntity(cube(i), 0, 1, 0)
If KeyDown( 203 )= True Then MoveEntity cam,-0.1,0,0
	If KeyDown( 205 )= True Then MoveEntity cam,0.1,0,0 
	If KeyDown( 208 )= True Then MoveEntity cam,0,-0.1,0 
	If KeyDown( 200 )= True Then MoveEntity cam,0,0.1,0 
	If KeyDown( 44 )= True Then MoveEntity cam,0,0,-0.1 
	If KeyDown( 45 )= True Then MoveEntity cam,0,0,0.1
Next
EndIf

RenderWorld()
Flip()
Wend
