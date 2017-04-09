Graphics3D(1280,1024,32,1)
SetBuffer(BackBuffer())

cam = CreateCamera()
	CameraClsColor(cam, 0,0,0)
	PositionEntity(cam, 0,10,-50)
lite = CreateLight()
	RotateEntity lite,90,0,0
mesh = CreateMesh() 
surf = CreateSurface(mesh) 

v0 = AddVertex (surf, -5,-5,0, 0 ,0) 
v1 = AddVertex (surf, 5,-5,0, 1 ,0) 
v2 = AddVertex (surf, 0, 5,0, 0.5,1) 



;PLANETOS:
	
	sune = LoadTexture("D:\Blitz\Planetos\sun.jpg")
	merkurye = LoadTexture("D:\Blitz\Planetos\merkury.jpg") 
	Venerae = LoadTexture("D:\Blitz\Planetos\venera.jpg")
	earthe = LoadTexture("D:\Blitz\Planetos\zeme.jpg")
	marse = LoadTexture("D:\Blitz\Planetos\marsas.jpg")
	jupitere = LoadTexture("D:\Blitz\Planetos\jupiter.jpg")
	saturne = LoadTexture("D:\Blitz\Planetos\saturn.jpg")
	neptunkae = LoadTexture("D:\Blitz\Planetos\neptunas.jpg")
	urankae = LoadTexture("D:\Blitz\Planetos\uranas.jpg")

x_scale#=1
y_scale#=1 
z_scale#=1 


Sun = CreateSphere()
	PositionEntity(sun, 1, 0, 0)
	EntityTexture sun, sune
	
Merkury = CreateSphere()
	PositionEntity(merkury, 5, 0, 0)
	EntityTexture merkury, merkurye
	
Venera = CreateSphere()
	PositionEntity(venera, 10, 0, 0)
	EntityTexture venera, venerae
	
Earth = CreateSphere()
	PositionEntity(earth, 15, 0, 0)
	EntityTexture earth, earthe
	
Mars = CreateSphere()
	PositionEntity(mars, 20, 0, 0)
	;EntityTexture mars, marse
	
Jupiter = CreateSphere()
	PositionEntity(jupiter, 25, 0, 0)
	EntityTexture jupiter, jupitere
	
Saturn = CreateSphere()
	PositionEntity(saturn, 30, 0, 0)
	EntityTexture saturn, saturne
	
Neptunka = CreateSphere()
	PositionEntity(neptunka, 35, 0, 0)
	EntityTexture neptunka, neptunkae
	
Uranka = CreateSphere()
	PositionEntity(uranka, 80, 0, 0)
	EntityTexture uranka, urankae

;plane = CreatePlane
;	PositionEntity(plane, 1, 1, 1)
;	EntityColor(plane, 0, 225, 50)
Terrain = CreateTerrain(128)
	PositionEntity(terrain, 1, 1, 1)
	EntityColor(Terrain, 10, 20, 70) 
space = LoadTexture ("D:\Blitz\Planetos\space.jpg")
	EntityTexture Terrain, space, plane
	frameTimer=CreateTimer(60) 
	

	i=0
	While(Not KeyDown(1))
	If KeyDown( 2 )=True Then x_scale#=x_scale#-0.1 
	If KeyDown( 3 )=True Then x_scale#=x_scale#+0.1 
	If KeyDown( 4 )=True Then y_scale#=y_scale#-0.1 
	If KeyDown( 5 )=True Then y_scale#=y_scale#+0.1 
	If KeyDown( 6 )=True Then z_scale#=z_scale#-0.1 
	If KeyDown( 7 )=True Then z_scale#=z_scale#+0.1
	;ScaleEntity sun,x_scale1,y_scale1,z_scale1

If KeyDown( 203 )= True Then MoveEntity cam,-0.1,0,0 
	If KeyDown( 205 )= True Then MoveEntity cam,0.1,0,0 
	If KeyDown( 208 )= True Then MoveEntity cam,0,-0.1,0 
	If KeyDown( 200 )= True Then MoveEntity cam,0,0.1,0 
	If KeyDown( 44 )= True Then MoveEntity cam,0,0,-0.1 
	If KeyDown( 45 )= True Then MoveEntity cam,0,0,0.1

	TurnEntity(saturn, 0, 5, 0)
	TurnEntity(merkury, 0, 5, 0)
	TurnEntity(venera, 0, 5, 0)
	TurnEntity(earth, 0, 5, 0)
	TurnEntity(mars, 0, 5, 0)
	TurnEntity(jupiter, 0, 5, 0)
	TurnEntity(neptunka, 0, 5, 0)
	TurnEntity(uranka, 0, 5, 0)
	TurnEntity(sun, 0, 5, 0)
		i=i+1
	x# = Sin(i)
	If i>360 Then i=1






		RenderWorld()
	Flip()
	Wend 