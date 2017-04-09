Graphics3D(800, 600, 16, 1)
SetBuffer(BackBuffer())

CreateTerrain()
p = CreatePlayer()
While (Not KeyDown(1))
	UpdatePlayer(p)
	RenderWorld
	Flip 
Wend

Function CreateTerrain()
	

	light = CreateLight()
	LightColor (light, 100, 100, 0)
	For i = 0 To 15
		For j = 0 To 15
			For k = 0 To 15
			c = CreateCube()
			PositionEntity (c, 10*j, 10*k, 10*i)
			EntityColor (c, 0, 0, 0)

		Next 
		Next 
Next 
End Function
Function CreatePlayer()
	p = CreateCamera()
	CameraClsColor (p, 0,0,100)
	
	Return p
End Function
Function UpdatePlayer(p)
	If KeyDown( 203 ) Then TurnEntity p,0,2,0; TurnLeft
	If KeyDown( 205 ) Then TurnEntity p,0,-2,0; TurnRight
	If KeyDown( 208 ) Then MoveEntity p,0,-1,0; Down
	If KeyDown( 200 ) Then MoveEntity p,0,1,0; Up
	If KeyDown( 44 ) Then MoveEntity p,0,0,-1;Backward  
	If KeyDown( 30 ) Then MoveEntity p,0,0,1; Forward
End Function