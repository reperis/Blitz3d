Graphics3D(1024, 768, 16, 1)
SetBuffer(BackBuffer())


CreateTerrain()
p = CreatePlayer()
While(Not KeyDown(1))
	Collisions(2, 1, 3, 2)
	UpdateWorld 
	UpdatePlayer(p)
  RenderWorld()
  ;PrintStatistics()
  Flip()
Wend

Function CreateTerrain()
 
	For i = 0 To 9
	  For j = 0 To 9
	  	For k = 0 To 9	
				c = CreateCube()
				EntityType(c, 1)
				s# = Rnd(1, 3)
				EntityRadius(c, 10)
				PositionEntity(c, 10*j, 10*k, 10*i)
				;ScaleEntity(c, s,s,s)
				;EntityBox(c, -s/2.0, -s/2.0, -s/2.0,s,s,s)
				EntityColor(c, Rnd (0, 150),0,0)
				EntityAlpha(c, Rnd(0, 1))
			Next
		Next		
	Next 
End Function
Function CreatePlayer()
  p = CreateSphere(16)
	c = CreateCamera(p)
	l = CreateLight(3, p)
	CameraFogMode(c, 1)
	CameraFogRange(c, 0, 150)
	CameraFogColor(c,255,255,255)
	CameraClsColor(c,255,255,255)
	PositionEntity(l, 0,0,-5)
	EntityAlpha(p, 0.5)
	LightRange(l,200)
	LightConeAngles(l, 20, 30)
	PositionEntity(c, 0, 0, -5)
	PositionEntity(p, 0, 0, -10)
	RotateEntity(p, 0, -45, 0)
	EntityColor(p, 150, 0, 0)
	EntityType(p, 2)
	EntityRadius(p, 1)
	Return p
	
	Return p
End Function
Function UpdatePlayer(p)
  If(KeyDown (200)) MoveEntity(p,  0,  0,  1)			; MOVE FORWARDS
  If(KeyDown (208)) MoveEntity(p,  0,  0, -1)			; MOVE BACKWARDS	
  If(KeyDown (203)) MoveEntity(p, -1,  0,  0)			; STRAFE LEFT
 	If(KeyDown (205)) MoveEntity(p,  1,  0,  0)			; STRAFE RIGHT
	TurnEntity(p, MouseYSpeed(), 0, 0, False)				; UP-DOWN
	TurnEntity(p, 0, - MouseXSpeed(), 0, True)			; LEFT-RIGHT
  MoveMouse(GraphicsWidth()/2, GraphicsHeight()/2); RECENTER
End Function
Function PrintStatistics()
  Text(0, 0, "(" + MouseX() + ", " + MouseY() + ", " + MouseZ() + ")")
  Text(0, 10, "(" + MouseXSpeed() + ", " + MouseYSpeed() + ", " + MouseZSpeed() + ")")	
End Function