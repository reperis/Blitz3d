Gosub makeSound

Type Control_Type
	Field Max_Bombs
	Field Bomb_Speed
	Field Crit_Drop_Speed
	Field drop_bomb_chance
	Field Player_StartSpeed
	Field Player_Speed
	Field Bullet_Speed
	Field Max_Rows
	Field Max_Cols
	Field XRes, YRes, Depth
	Field Beast_Speed#
	Field Beast_Speed_Increment#
	Field UFO_Speed
	Field Level
	Field NextExtraLife
End Type

Type points_type
	Field x,y
	Field id
End Type

Type explosion_type
	Field x,y
	Field speed
	Field status
	Field dirx,diry
	Field count
	Field id
End Type

Type HighScoreTable_Type
	Field score
	Field name$
End Type

Dim HighScores.HighScoreTable_type(10)
Global highscore.highscoretable_type
Dim SpaceyFont(40)

Type Player_Type
	Field x,y
	Field moved
	Field lives
	Field score
	Field Shield_Active
	Field Shield_Timer#
	Field Non_Expiring_Shot
	Field NES_Timer# ; Non-expiring shot timer
	Field Cluster
	Field Cluster_Timer#
	Field Speedy
	Field Speedy_Timer#
End Type

Type stars_type
	Field image
	Field x,y
	Field count
End Type

Type Beast_Type
Field x#,y#
Field deleted
End Type

Type Bullet_Type
	Field fired
	Field x,y
End Type

Type Bomb_Type
	Field fired
	Field x,y
	Field deleted 
End Type

Type UFO_Type
	Field x,y
	Field status
End Type

Type Bonus_Type
	Field active
	Field x,y
	Field frame
	Field frametimer#
End Type

;*********************************
; Create and Set up Control Type
; This is used to manage all our game control variables,
; it's easier keeping them in one place and putting the set up at the start means we can 
; control the details in the rest of the set up from here on in

Global control.Control_Type
Set_Up_Control

; Set graphics size
Graphics control\xres,control\yres,control\depth
AutoMidHandle True

Dim im(15)
Dim bases(3)
Dim Points_Ims(16)
Dim debri(16)
Dim stars.stars_type(3)

Global bim 
Global ufo_im
Global ship_im 
Global bonus_im
Global base_Im
Global bomb_im = CreateImage(2,32)
Global xp,yp
Global blank_im = CreateImage(6,24)
Global point.points_Type
Global bullet_im = CreateImage(2,16)
Global dir# = control\beast_speed
Global score = 0
Global beast_count, total_beasts
Global x#, y#
Global EndGame = False
Global char$ = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 @#~"

Global explosion.Explosion_type
Global player.Player_Type
Global beast.Beast_Type
Global bullet.bullet_type
Global bombs.bomb_type
Global ufo.ufo_type = New ufo_type
Global Bonus.Bonus_type = New Bonus_type
player.Player_Type = New Player_Type	; create player

Make_Base_Image
Make_Bonus_Images
Make_Collision_Image
Make_Player_Image
Make_Beast_Images
Make_Beasts
Make_Bases
Make_Points_Ims
Make_Blank
Make_Bombs
Make_Bullets
Make_Debri
Make_Stars
Make_Ship
Make_Ufo
Make_Player
Make_Bullet
Make_HighScoreTable
Make_SpaceyFont

SetBuffer BackBuffer()

quit = displaystartscreen()

While quit = False

	endgame = False
	
	old_level = 0 : control\level = 1 : control\nextextralife = 2500
	make_player	
	While endgame = False

		If old_level <> control\level Then
			Introduce_Level
			StartNewLevel
			old_level = control\level
		End If
		
		If player\lives<=0 Then endgame =True 
		
		time# = MilliSecs()
		
		If player\score > control\nextextralife Then
			control\nextextralife = control\nextextralife + 2500
			player\lives = player\lives + 1
		End If

		Move_stars	
		Display_Info
		af = (af + 1) And 15
	
		min_x = control\xres
		max_x = 0

		new_ufo
		If ufo\status = True Then move_ufo

		bomb_count = 0
		For bombs = Each bomb_type
			bomb_count = bomb_count + 1
			DrawImage bomb_im,bombs\x,bombs\y
			bombs\y = bombs\y + control\bomb_speed
		
			If ImagesCollide(bomb_im,bombs\x,bombs\y,0,bases(0),192,400,0) Then
				SetBuffer ImageBuffer(bases(0))
				Color 0,0,0
				bxp = bombs\x - 168
				byp = bombs\y -368
				draw_blank bxp,byp
				SetBuffer BackBuffer()
				Delete bombs
				bomb_count = bomb_count-1
			ElseIf ImagesCollide(bomb_im,bombs\x,bombs\y,0,bases(1),320,400,0) Then
				SetBuffer ImageBuffer(bases(1))
				Color 0,0,0
				bxp = bombs\x - 288
				byp = bombs\y -368
				draw_blank bxp,byp	
				SetBuffer BackBuffer()
				Delete bombs
				bomb_count = bomb_count-1
			ElseIf ImagesCollide(bomb_im,bombs\x,bombs\y,0,bases(2),448,400,0) Then
				SetBuffer ImageBuffer(bases(2))
				Color 0,0,0
				bxp = bombs\x - 420
				byp = bombs\y -368
				draw_blank bxp,byp
				SetBuffer BackBuffer()
				Delete bombs
				bomb_count = bomb_count-1
			ElseIf ImagesCollide(bomb_im,bombs\x,bombs\y,0,ship_im,player\x,player\y,0) Then
				If player\shield_Active = False Then
					bomb_count=bomb_count-1
					Player\lives = player\lives-1
					For i = 0 To 10
						ClsColor Rnd(255),Rnd(255),Rnd(255)
						Cls
						Flip
					Next
				End If
				Delete bombs
			Else If bombs\y > 480 Then
				Delete bombs : bomb_count=bomb_count-1
			End If
			
		Next

	
		check_bullets_and_bases
	
		player\moved = False
		
		If MilliSecs()>player\shield_timer + 5000 Then
			player\shield_active = False
		Else
			Text 0,128,"Shield Active"
		End If
		
		If MilliSecs()>player\NES_Timer + 5000 Then
			player\Non_Expiring_Shot = False
		Else
			Text 0,144,"Non Expiring Shots"
		End If 
		
		
		If MilliSecs()>player\cluster_timer + 5000 Then
			player\cluster = False
		Else
			Text 0,160,"Cluster Fock"
		End If
		
		If MilliSecs()>Player\Speedy_Timer + 5000 Then
			control\player_speed = control\player_startspeed
			player\speedy = False
		Else
			Text 0,176,"Speedy"
		End If
		
		bomb_dropped = False
	
		beast_hit = False

		dokeys

		beast_count = 0
		For beast = Each beast_Type
			If beast\deleted = False Then
				beast_count = beast_count + 1
				x = beast\x + dir : y = beast\y
				If inc_y = True Then y = y + control\crit_drop_speed
				If dir > 0 Then
					If x > max_x Then max_x = x
				Else
					If x < min_x Then min_x = x
				EndIf
				
				If ImagesCollide(im(af), beast\x,beast\y,0,bases(0),192,400,0) = True Then 
					FreeImage bases(0) : bases(0) = CreateImage(1,1)
				Else If ImagesCollide(im(af), beast\x,beast\y,0,bases(1),320,400,0) = True Then
					 FreeImage bases(1) : bases(1) = CreateImage(1,1)
				Else If ImagesCollide(im(af), beast\x,beast\y,0,bases(2),448,400,0) = True Then
					FreeImage bases(2) : bases(2) = CreateImage(1,1)
				End If				
				beast\x = x
				beast\y = y

				DrawImage im(af),beast\x, beast\y
	
				If bullet\fired = True Then
					If ImagesCollide(im(af),beast\x,beast\y,0,bim,bullet\x,bullet\y,0) Then

						Add_Point beast\x , beast\y
						player\score = player\score + 10
					
						beast_count = beast_count-1 : beast_hit = True
					
						Make_Explosion beast\x,beast\y,5	
									
						beast\deleted = True
						
						If player\cluster = True Then 
							bullet\x = bullet\x + (Rand(128)-64)
							bullet\y = bullet\y + (Rand(128)-64)
						Else If player\non_expiring_shot = False Then 
							bullet\fired = False
						End If
						
					EndIf
				EndIf
			
				If bomb_dropped = False Then
					If bomb_count < control\max_bombs Then
						If Rand(100)<control\drop_bomb_chance Then
							bomb_count = bomb_count+1
							bomb_dropped = True
							bombs = New bomb_type
							bombs\x = beast\x : bombs\y = beast\y
						EndIf
					EndIf
				EndIf
				
			EndIf
			
			If beast\y > 460 Then endgame = True
		Next
	
		If beast_hit = True Then Increase_Beast_Speed
  
		inc_y = False
		If dir >0 Then
			If max_x > 608 Then dir = -dir : inc_y = True
		Else
			If min_x < 32 Then dir = - dir : inc_y = True
		EndIf

		Do_Points	
		DoExplosion
		Move_Bonus_Item
		If ufo\status = True Then Check_Bullets_And_Ufo
		Move_And_Draw_Bullet
	
		If beast_count = 0 And ufo\status = False Then control\level = control\level + 1
		
		DrawImage ship_im,player\x,player\y

		DrawImage bases(0),192,400
		DrawImage bases(1),320,400
		DrawImage bases(2),448,400
	
		Color 255,255,255
	
		Flip
	Wend
	
	DisplayEndScreen
	EnterHighScore
	endgame = False
		
	Delay(500)
	FlushKeys
	
	quit = displaystartscreen()	
Wend
End

Function Set_Up_Control()
control.Control_Type = New Control_Type
control\max_bombs = 4
control\bomb_speed = 4
control\crit_drop_Speed = 4
control\drop_bomb_chance = 5
control\player_StartSpeed = 4
control\player_speed = control\player_StartSpeed
control\bullet_speed = 24
control\max_rows = 7
control\max_cols = 11
control\xres = 640 ; Try this pair at different resolution: 640,480 or 1024,768 or 1280,1024
control\yres = 480; 
control\depth = 16;
control\level = 1
control\Beast_Speed = 1
control\Beast_Speed_Increment = 0.05
control\UFO_Speed = 4
End Function

Function Do_Points()
For point = Each points_type
	DrawImage points_Ims(point\id),point\x,point\y
	point\id = point\id + 1
	If point\id > 15 Then Delete point
Next
End Function

Function Make_Ship()
ship = CreateImage(64,16)
SetBuffer ImageBuffer(ship)
Rect 0,8,64,8,True
Rect 16,4,32,4,True
Rect 24,0,16,4,True
SaveImage ship,"images\ship01.bmp"
End Function

Function DoKeys()

If KeyDown(57) Then
	If bullet\fired = False Then
		bullet\x = player\x : bullet\y = 460
		bullet\fired = True
		PlaySound shootsound
	EndIf
EndIf

If player\moved = False Then
	If KeyDown(205) Then
		If player\x < 608 Then player\x = player\x + control\player_speed : player\moved = True
	EndIf
		
	If KeyDown(203) Then
		If player\x > 16 Then player\x = player\x - control\player_speed : player\moved = True
	EndIf
EndIf

If KeyDown(1) Then EndGame = True

End Function

Function DoExplosion()
For explosion = Each explosion_type
	DrawImage debri(explosion\id), explosion\x, explosion\y
	Select explosion\status
		Case 0
			explosion\dirx = Int(Rand(2))-1
			explosion\diry = Int(Rand(2))-1
			explosion\status = 1
			explosion\count = 10
		Case 1
			explosion\x = explosion\x + explosion\dirx
			explosion\y = explosion\y + explosion\diry
			explosion\count=explosion\count - 1 
			If explosion\count<=0 Then explosion\status = 2
		Case 2
			explosion\y=explosion\y+explosion\speed
			If explosion\y > 480 Then Delete explosion
		Default
			; There should be no other options so delete the explosion
			Delete explosion
	End Select		
Next
End Function

Function Check_Bullets_And_UFO()
If bullet\fired = True Then
	If ufo\status = True Then
		If ImagesCollide(ufo_Im,ufo\x,ufo\y,0,bullet_im,bullet\x,bullet\y,0) Then
			ufo\status = False
			Make_Explosion ufo\x,ufo\y,25
			player\score = player\score + 50
			If bonus\active = False Then
				bonus\active = True
				bonus\x = ufo\x : bonus\y = ufo\y
			End If
		EndIf
	EndIf
EndIf
End Function

Function Move_And_Draw_Bullet()
bullet\y = bullet\y - control\bullet_Speed
DrawImage bullet_im,bullet\x,bullet\y
End Function

Function Check_Bullets_And_Bases()
	If bullet\fired = True Then
		For i = 0 To 2
			If ImagesCollide(bases(i),192+(i*128),400,0,bullet_im,bullet\x,bullet\y,0) Then
				If player\Non_Expiring_Shot = False Then bullet\fired = False
				SetBuffer ImageBuffer(bases(i))
				xp = bullet\x - (160 + (i * 128))
				yp = bullet\y - 384
				draw_Blank xp,yp
				SetBuffer BackBuffer()
			EndIf
			If bullet\fired = False Then Exit
		Next

		If bullet\y < 0 Then 
			bullet\fired = False
		EndIf
	EndIf
End Function

Function Make_Points_Ims()
For i = 0 To 15
	Points_Ims(i) = CreateImage(32,32)
	Color 0,255-(i Shl 4), 0
	SetBuffer ImageBuffer(Points_Ims(i))
	Oval 16-i,16-i,i Shl 1 ,i Shl 1,False
	Text 16,16,"10",True,True
Next	
End Function

Function Add_Point(x,y)
	point = New points_type
	point\x = x : point\y = y
	point\id = 0
End Function

Function Move_Stars()

	stars(0)\y = (stars(0)\y -1) And 255
	TileBlock stars(0)\image,stars(0)\x,stars(0)\y

	stars(1)\y = (stars(1)\y-2) And 255
	TileImage stars(1)\image,stars(1)\x+4,stars(1)\y

	stars(2)\y = (stars(2)\y-4) And 255
	TileImage stars(2)\image,stars(2)\x,stars(2)\y

End Function

Function Make_Stars()

stars(0) = New stars_type
stars(0)\image = CreateImage(256,256)
stars(0)\count = Rand(256)
stars(0)\x = Rand(256)
SetBuffer ImageBuffer(stars(0)\image)
Color 64,64,255
For i = 0 To 31
	x = Rand(256)
	y = Rand(256)
	Rect x,y,1,1,True
Next

stars(1) = New stars_type
stars(1)\count = Rand(256)
stars(1)\x = Rand(256)
stars(1)\image = CreateImage(256,256)
SetBuffer ImageBuffer(stars(1)\image)
Color 128,128,128
For i = 0 To 16
	x = Rand(256)
	y = Rand(256)

	Rect x,y,1,1,True
Next

stars(2) = New stars_type
stars(2)\count = Rand(256)
stars(2)\x = Rand(256)
stars(2)\image = CreateImage(256,256)
SetBuffer ImageBuffer(stars(2)\image)
Color 255,255,255

For i = 0 To 15
	x = Rand(256)
	y = Rand(256)
	Rect x,y,1,1,True
Next

End Function

Function New_Ufo()

If ufo\status = False Then
	If Rand(100) < 10 Then
		ufo\status = True
		ufo\x = 640
		ufo\y = 32
	EndIf
EndIf

End Function

Function Move_Ufo()
	ufo\x = ufo\x - control\ufo_speed
	If ufo\x < 0 Then
		ufo\status = False
	EndIf
	DrawImage ufo_im,ufo\x,ufo\y
End Function

Function Make_Ufo_Old()
	ufo_im = CreateImage(64,16)
	Color 0,255,0
	SetBuffer ImageBuffer(ufo_im)
	Oval 32,8,32,8,True
End Function

Function Draw_Blank(xp,yp)
	MaskImage blank_im,0,0,0
	DrawImage blank_im,xp,yp
End Function

Function Make_Blank()
	SetBuffer ImageBuffer(blank_im)
	MaskImage blank_im,255,0,255
	Color 255,0,255
	Rect 0,0,6,24,True
End Function

Function Make_Collision_Image()
bim = CreateImage(16,16)
SetBuffer ImageBuffer(bim)
ClsColor 0,0,0
Cls
Color 255,255,255
Oval 8,8,8,8,True
SetBuffer BackBuffer()
End Function

Function Make_Debri()
; This function creates the images used for the debri of the beasts ships
; It creates 15 images to choose from to use for falling debri by
; creating an image, setting the draw buffer to the image buffer, setting a random colour,
; and finally drawing a solid rectangle in that color
For i = 0 To 15
	debri(i) = CreateImage(2,2)
	SetBuffer ImageBuffer(debri(i))
	Select Rand(3)
		Case 0
			Color Rand(255),0,0
		Case 1
			Color 255,Rand(255),0
		Case 2
			Color 255,255,255
		Default
			Color 255,255,0
	End Select
	Rect 0,0,2,2,True
Next
End Function

Function Make_Player()
; This creates the player
player\x = 320 : player\y = 460			; position player on screen
player\score = 0
player\lives = 3
End Function

Function Make_Beasts()
; this produces a grid of beasts and puts them into their correct positions on screen
Delete Each beast_type
total_beasts = 0
beast_count = 0
For y = 0 To control\max_rows		; go through the rows
	For x = 0 To control\max_cols	; go through the columns
		beast = New beast_type		; create a new beast
		beast\x = x * 32.00			; place it at position using logical arithmetic, equivalent to x * 32
		beast\y = y * 32.0 + 64.0		; place it at position using logical arithmetic, equivalent to (y * 32)+32
		beast_count = beast_count + 1	; increase the number of beasts
	Next
Next

total_beasts = beast_count	; we need to know how many there originally were

End Function

Function Make_Bullet()
; This creates the players bullet used throughout the whole game
	bullet = New bullet_type	; create bullet
	bullet\fired = False		; set that it's not been fired
End Function

Function Increase_Beast_Speed()
If dir < 0 Then 
	dir = dir - control\beast_speed_increment	
Else
	dir = dir + control\beast_speed_increment	
EndIf
End Function

Function Make_Explosion(x,y,sparks)
For i = 0 To sparks
	explosion = New explosion_type
	explosion\x = x + Rand(50)-25
	explosion\y = y + Rand(50)-25
	explosion\speed = Rand(10)+1
	explosion\status = 0
	explosion\id = Rand(15)
Next
End Function

Function DisplayStartScreen()
SetBuffer BackBuffer()
screentimer# = MilliSecs()
display_highscore = False

done = False : quit = False
While Not done
	Cls
	move_stars
	If KeyDown(1) Then done = True : quit = True
	If KeyDown(57) Then done = True
	If display_highscore = False Then 
		SpaceyText 400,100,"SPACE INVADERS",True
		SpaceyText 400,164,"SPACE TO BEGIN",True
		SpaceyText 400,196,"ESC TO EXIT",True
	Else
		SpaceyText 400,100,"High Score Table",True
		For i = 0 To 9
			SpaceyText 200,116 + (i Shl 4), highscores(i)\score, False
			SpaceyText 400,116 + (i Shl 4), highscores(i)\name, False
		Next
	End If
	
	If MilliSecs()>(screentimer+5000) Then 
		screentimer = MilliSecs()
		display_highscore = Not(display_highscore)
	End If
	Flip	
Wend

Return quit
FlushKeys
End Function

Function Display_Info()
	Text control\xres - (Len(Str(player\score))*16),0,player\score
	For i = 0 To (player\lives-1)
		DrawImage ship_im, 32 + i Shl 6, 16
	Next 
End Function

Function DisplayEndScreen()
	FlushKeys
	screentimer = MilliSecs()
	While MilliSecs()<screentimer+2000
		Cls
		Move_Stars
		SpaceyText 200,400,"GAME OVER",True
		Flip
	Wend
	FlushKeys
End Function

Function Make_SpaceyFont()
For i = 0 To 39
	SpaceyFont(i) = CreateImage(32,48)
	MidHandle SpaceyFont(i)
	SetBuffer ImageBuffer(SpaceyFont(i))
	ch$ = Mid$(char$, i+1,1)
	Select ch
		Case "@"
			Text 4,8,"DEL"
		Case "#"
			Text 4,8,"END"
		Case "~"
			Rect 0,0,32,32,False
		Default 
			Text 12,8,ch$
	End Select
Next
End Function

Function EnterHighScore()
name$ = ""
pos = 10
For i = 9 To 0 Step -1
	If player\score>highscores(i)\score Then pos = i
Next

If pos<10 Then
	done = False
	
	x = 0 : y = 0

	keypresstimer# = MilliSecs()
	keypressed = False
	
	While Not(done)
		Cls
		Move_Stars
		yp = 0 : xp = 0
		For i = 0 To 38
			DrawImage SpaceyFont(i),160 + xp * 32, 100 + yp * 32
			xp = xp + 1 : If xp>9 Then xp = 0 : yp=yp+1
		Next	
				
		If keypressed = False Then
			If KeyDown(203) Then x = x - 1 : keypressed = True : If x < 0 Then x = 0
			If KeyDown(205) Then x = x + 1 : keypressed = True : If x > 9 Then x = 9
			If KeyDown(200) Then y = y - 1 : keypressed = True : If y < 0 Then y = 0
			If KeyDown(208) Then y = y + 1 : keypressed = True : If y > 3 Then y = 3

			If KeyDown(57) Then
				keypressed =True
				ch = (x + y*10)+1
				Select Mid(char$,ch,1)
					Case "@"
						If Len(name)>0 Then
							name = Left(name, Len(name)-1)
						End If
					Case "#"
						done = True
					Case "~"
					Default
						If Len(name)<5 Then name = name + Mid(char$,ch,1)
				End Select
			End If			

			If keypressed = True Then keypresstimer = MilliSecs()

		End If
	
		If MilliSecs()>keypresstimer + 100 Then keypressed = False
		
		If ((x) + (y*10)) > 38 Then x = x -1		
		DrawImage SpaceyFont(39), 160 + x * 32, 100 + y * 32
		
		SpaceyText 100,400,name,True
		If KeyDown(1) Then done = True
		Flip
	Wend
		
	For i = 8 To pos Step -1
		highscores(i+1)\score = highscores(i)\score
		highscores(i+1)\name = highscores(i)\name
	Next
	
	highscores(pos)\score = player\score
	highscores(pos)\name = name
End If
End Function

Function Make_Highscoretable()
For i = 9 To 0 Step -1
	highscores(i) = New highscoretable_type
	highscores(i)\score = score
	highscores(i)\name = "Bob"
	score = score + 100
Next
End Function

Function StartNewLevel()
	Delete Each bomb_type
	Make_Beasts
	Make_Bases
	control\Beast_Speed = 1+ (control\level/2)
	dir = control\beast_speed
End Function

Function Introduce_Level()
timer# = MilliSecs()
While MilliSecs()<timer + 500
	Cls
	Move_Stars
	SpaceyText 300,200,"Level : " + control\level, True
	Flip
Wend
End Function

Function Make_Player_Image()
ship_im = CreateImage(50,50)
SetBuffer ImageBuffer(ship_im)
;ClsColor 0,0,0
;Color 255,255,255
;Rect 24,0,16,4,True
;Rect 16,4,32,4,True
;Rect 8,8,48,4,True
;Rect 0,12,64,4,True
;ship
;drawing of player ship
For i = 1 To 15
	Color 100-(i*5),100-(i*5),100-(i*5)

	Line 15-(i/10),15+i,35+(i/10),15+i ;front underchasi
Next
For i = 1 To 15
	Color 100-(i*5),100-(i*5),100-(i*5)

	Line 14+i/4,30+i,36-i/4,30+i ;rear under chassi
Next 
For i = 1 To 10
	Color 100-(i*5),100-(i*5),100-(i*5)

	Line 23-(i/2),0+i,27+(i/2),0+i ;nose
Next
For i = 1 To 15
	Color 80-(i*5),80-(i*5),80-(i*5)

	Line 17+(i/7),10+i,33-(i/7),10+i ;neck	
Next
For i = 1 To 15
	Color 80+i,80+i,80+i

	Line 23-i,17+i,27+i,17+i ;front of wing
Next
For i = 1 To 5
	Color 80-i,80-(i*2),80-i

	Line 10,32+i,40,32+i ;rear of wing
Next
For i = 1 To 5
	Color 80-(i*5),80-(i*5),80-(i*5)

	Line 20,45+i,30,45+i
Next 
For i = 1 To 3
	Color 100-(i*15),100-(i*15),100-(i*15)
	Line 6-i,15,6-i,40         ; rockets
	Line 6+i,15,6+i,40

	Line 44-i,15,44-i,40
	Line 44+i,15,44+i,40
Next
For i = 1 To 5
	Color 50,50,50
	Line 3+(i/2),15-i,9-(i/2),15-i
	Line 41+(i/2),15-i,47-(i/2),15-i
Next 
Line 3,15,9,15
Line 41,15,47,15
Color 120,120,120 ;rocket detail
Line 6,10,6,40
Line 44,10,44,40
For i = 1 To 4
Color 60-i,60-i,60-i
	Line 3,41,9,41
	Line 41,41,47,41
	Line 3-Floor(i/2),41+i,9+Floor(i/2),41+i  ;rocket flares
	Line 41-Floor(i/2),41+i,47+Floor(i/2),41+i
Next
Color 120,120,120
Line 6,41,6,45 ;left flare details
Line 3,41,1,45
Line 9,41,11,45
Line 44,41,44,45 ;Right flare details
Line 41,41,39,45
Line 47,41,49,45
 ;ship highlights and little details
Color 140,50,50
Line 23,17,27,17
For i = 1 To 5
	Color 120-(i*10),50,50   ;window with red light reflection on nose
	Line 23-i,16-i,27+i,16-i
Next

For i = 1 To 12
	Color 80+i*2,80+i*2,80+i*2 
	Line 23-i,20+i,27+i,20+i
Next

For i = 10 To 40 Step 5 ;some back detail to break up the color a little
	Color 120,120,120
	Line 0+i,32,0+i,37
Next 
;For i = 1 To 3
	;Color 100-(i*15),100-(i*15),100-(i*15)
	;Line 25-i,20,25-i,45-i
	;Line 25+i,20,25+i,45-i
;Next 
;Color 120,120,120 
ScaleImage ship_im,.75,.75
SetBuffer BackBuffer()
End Function

Function Make_Beast_Images()
For i = 0 To 15

	im(i) = CreateImage(50,50)
	SetBuffer ImageBuffer(im(i))

For l = 1 To 4
	Color 50+(l*5),100+(l*5),50+(l*5)
	Line 25+l,0+(l*2),25+l,25-(l*2)  ;center body
	Line 25-l,0+(l*2),25-l,25-(l*2)
Next 
For l = 1 To 8
	Color 50+(l*5),100+(l*5),50+(l*5)
	Line 30+l,10+l,30+l,15+l ;Right wing
	Line 20-l,10+l,20-l,15+l ;left wing
Next 

Color 20,60,20
Line 30,10,30,15 ;Right wing start
Line 20,10,20,15 ;left wing start
For l = 1 To 3
	Color 50-(l*5),100-(l*10),50-(l*5)
	Line 10+l,10+l,10+l,35-(l*3)	;left rocket
	Line 10-l,10+l,10-l,35-(l*3)
	Line 40+l,10+l,40+l,35-(l*3)	;right rocket
	Line 40-l,10+l,40-l,35-(l*3)
Next 

Color 30,30,30
For l = 1 To 5

	Line 25+Ceil(l/2),23-l,25-Ceil(l/2),23-l
Next 
Color 50,100,50
Plot 25,23
Line 25,0,25,17     ;center body
Line 10,10,10,35	;left rocket
Line 40,10,40,35	;right rocket



Next


For i = 0 To 15
	ScaleImage im(i),.75,.75
Next
SetBuffer BackBuffer()
End Function

Function Make_Base_Image()
base_im = CreateImage(64,32)
MaskImage base_im,255,0,255
SetBuffer ImageBuffer(base_im)
Color 64,64,64
Rect 0,0,64,64,True
Color 255,0,255
For s = 0 To 15
	Line s,0,0,15-s
	Line 48+s,0,64,s
Next
End Function

Function Make_Bases()
For i = 0 To 3
	bases(i) = CopyImage(base_im)
	MaskImage bases(i),255,0,255
Next	
End Function

Function Make_Bombs()
SetBuffer ImageBuffer(bomb_im)
Color 0,255,0
For i = 0 To 31 
	If i And 1 Then Plot 0,i Else Plot 1,i
Next 
End Function

Function Make_Bullets()
SetBuffer ImageBuffer(bullet_im)
Color 0,255,255
For i = 0 To 15
	If i And 1 Then Plot 0,i Else Plot 1,i
Next 
End Function

Function Make_Ufo()
	ufo_im = CreateImage(100,100)
	;Color 0,255,0
	SetBuffer ImageBuffer(ufo_im)
	;rockets
For i = 1 To 3
	Color 75-(10*i),75,75-(10*i)
	Line 25+i,60,25+i,95-(i*5) ;left rocket
	Line 25-i,60,25-i,95-(i*5)
	Line 75+i,60,75+i,95-(i*5) ;right rocket
	Line 75-i,60,75-i,95-(i*5)
Next
Line 25,60,25,70
Line 75,60,75,70
Color 175,75,75
Line 25,65,25,95
Line 75,65,75,95
;mid section
For i = 1 To 4 ;neck
 	Color 40,80,40
	Line 50+i,20+(i*7),50+i,100-(i*7) 
	Line 50-i,20+(i*7),50-i,100-(i*7)
Next 
For i = 1 To 4 ;neck highlight
	Color 70,100,70
	Line 50+i,20+(i*7),50+i,90-(i*7) 
	Line 50-i,20+(i*7),50-i,90-(i*7)
Next 
For i = 1 To 3
	Line 50+i,0+(i*10),50+i,60 
	Line 50-i,0+(i*10),50-i,60
Next
 
;wings
For i = 1 To 25
	Color 55+i,75+(i*2),25+i
	Line 50-i,40+i,50-i,60+i
	Line 50+i,40+i,50+i,60+i
Next 
;wing tips
Line 75,65,75,84
Line 25,65,25,84
For i = 1 To 25
	;Color 125-i,150-i,125-i
	Color 80-i,130-(i*3),50-i
	Line 75+i,65-i,75+i,85-Ceil(i*1.75)
	Line 25-i,65-i,25-i,85-Ceil(i*1.75)
Next 
;head
Color 30,80,30
For i = 1 To 10
	Line 50+Ceil(i/2),100-i,50-Ceil(i/2),100-i ;forhead
Next 
For i = 1 To 5
	Line 45+i,90-i,55-i,90-i 
Next
Color 25,25,25
For i = 1 To 5
	Line 50+Ceil(i/2),97-i,50-Ceil(i/2),97-i ;forhead
Next 
Color 175,75,75
Line 50,0,50,10 
;center body and rocket centers
Color 50,70,20
Line 50,38,50,63 ;wings center line
Color 70,90,70
Line 50,10,50,86
For i = 1 To 75 Step 6
Color 175-i,75-i,75-i
Plot 50,0+i
Next 
Color 70,90,70
Line 51,56,75,80
Line 49,56,25,80
Line 90,55,75,80
Line 10,55,25,80

ScaleImage ufo_im,.5,.25
RotateImage ufo_im,90	
End Function

Function SpaceyText(x,y,message$,centre)
If Len(message)>0 Then
	If centre = True Then
		length = Len(message) * ImageWidth(spaceyfont(0))
		xs = (640-length)/2
	Else
		xs = x
	End If
	
	For c = 1 To Len(message)
		v = 0
		ch$ = Mid$(Upper(message), c, 1)
		If ch>="0" And ch<="9" Then 
			v = 26 + Asc(ch)-48
		Else If ch>="A" And ch<="Z" Then 
			v = Asc(ch)-65
		Else
			v = 36
		End If
		
		DrawImage spaceyfont(v),xs + (c * ImageWidth(spaceyfont(0))), y
	Next			
End If
End Function

Function Make_Bonus_Images()
Bonus_im = CreateImage(64,16,8)
For i = 0 To 7
	SetBuffer ImageBuffer(bonus_im,i)
	ClsColor i Shl 5,0,0
	Cls
	Color 64,192,64
	Rect 0,0,64,16,False
	Text 8,2,"BONUS"
	SetBuffer BackBuffer()
Next
End Function

Function Move_Bonus_Item()
If bonus\active = True Then
	bonus\y = bonus\y + 4
	If MilliSecs() > bonus\frametimer + 100 Then
		bonus\frame = (bonus\frame + 1) And 7
		bonus\frametimer = MilliSecs()
	End If
	DrawImage bonus_im,bonus\x, bonus\y, bonus\frame
	If ImagesCollide(bonus_im, bonus\x,bonus\y,0, ship_im, player\x, player\y,0) Then
		bonus\active = False
		Choose_Bonus_Type
	End If
	If bonus\y > 600 Then bonus\active = False
End If
End Function

Function Choose_Bonus_Type()
bt = Int(Rand(6))+1
Select bt
	Case 1
		; Shield
		player\Shield_Active = True
		player\Shield_Timer = MilliSecs()
	Case 2
		; Non-expiring shot
		Player\Non_Expiring_Shot = True
		Player\Nes_Timer = MilliSecs()
	Case 3
		; Speed
		Player\Speedy = True
		Player\Speedy_Timer = MilliSecs()
		control\Player_speed = control\Player_StartSpeed Shl 1
	Case 4
		; Cluster fock
		Player\cluster = True
		Player\Cluster_timer = MilliSecs()
	Case 5
		; Rebuild bases
		Make_Bases		
	Case 6
		player\lives = player\lives + 1
		; Extra Life
End Select

End Function

;Becky Roses wierd way of doing sound in code
.shoot
Data 4810
Data 82,73,70,70,194,18,0,0,87,65,86,69,102,109,116,32,18,0,0,0,1,0,1,0,64,31,0,0,64,31,0,0,1,0,8,0,0,0,102,97,99,116,4,0,0,0,143,18,0,0,100,97,116,97,143,18,0,0,126,163,97,101,124,137
Data 134,110,112,120,154,131,117,116,152,132,141,155,80,104,221,66,135,200,59,112,244,129,56,188,155,61,114,208,47,88,198,89,59,162,204,46,109,207,113,84,189,194,40,111,234,125,51,146,215,68,72,202,156,43,97,207,127,88,109,205,90,93,121,204
Data 89,87,127,220,84,85,133,211,84,93,122,200,88,79,115,193,116,70,94,177,164,64,73,160,214,61,60,138,207,126,54,95,161,210,79,49,135,199,148,77,76,136,210,159,38,91,170,192,141,58,107,136,197,143,41,83,146,209,145,64,80,142,186,199
Data 100,63,121,150,205,143,43,88,142,180,188,82,41,95,163,199,191,92,57,113,153,184,181,81,41,112,145,183,205,113,36,104,128,162,203,152,73,60,117,143,182,191,129,45,52,118,160,178,185,153,48,51,131,139,168,193,153,93,44,89,148,152,172,188
Data 140,73,48,100,144,161,171,191,160,88,41,78,136,159,155,183,182,149,69,43,93,139,153,155,183,178,163,88,36,81,114,155,158,155,179,172,146,76,43,50,121,155,159,164,160,170,169,142,78,38,52,103,151,166,159,157,160,166,160,140,83,38,55,82
Data 142,165,162,163,147,154,165,154,145,114,55,44,53,103,148,168,169,161,155,145,145,156,151,144,125,106,60,49,58,85,134,156,174,171,161,158,145,140,136,143,148,140,132,119,116,34,28,111,226,100,125,185,106,178,115,102,160,91,132,162,107,188,85,106
Data 102,96,186,91,163,121,92,179,86,150,130,92,163,88,155,126,92,153,87,174,104,146,124,92,194,87,180,92,98,100,95,198,79,201,79,126,144,121,152,88,192,88,195,83,112,81,113,151,87,147,90,201,82,204,82,144,112,97,101,100,177,86,157,89
Data 203,83,198,84,178,91,195,86,142,107,170,93,118,130,147,103,109,147,135,111,103,156,125,107,92,154,115,109,99,149,92,104,107,136,98,99,122,113,105,90,144,98,127,85,187,90,168,87,207,85,210,97,160,94,171,119,104,118,110,188,86,184,86,184
Data 88,179,89,106,120,101,124,96,203,84,207,87,126,106,111,114,86,200,85,205,101,118,113,86,201,83,203,103,106,121,97,204,86,174,92,97,164,90,205,98,120,114,100,161,88,163,97,115,199,85,197,93,101,174,90,209,108,106,163,90,147,106,104,157
Data 81,134,102,100,199,88,122,121,84,173,97,110,210,88,134,113,89,177,101,109,171,89,121,132,95,142,113,100,212,101,106,198,86,110,177,86,157,158,86,168,101,88,170,98,102,171,97,104,208,98,100,210,85,101,209,92,112,192,92,107,208,85,102,207
Data 87,110,179,95,104,205,116,95,169,105,87,134,135,91,117,197,83,121,203,92,105,203,121,92,143,128,84,118,204,82,103,172,106,99,153,189,82,119,209,106,99,147,126,78,112,203,84,88,129,162,84,106,171,107,94,124,210,86,108,130,189,83,117,144
Data 146,87,124,164,117,90,129,185,105,93,129,202,96,97,131,203,96,97,130,206,100,94,126,210,108,90,125,196,135,81,121,166,182,75,113,143,211,85,101,131,169,115,85,122,137,195,78,105,126,176,109,85,123,136,208,79,103,124,150,158,79,103,130,176
Data 116,76,112,135,193,152,77,116,137,159,142,77,114,129,156,155,73,101,132,144,192,87,91,127,140,205,126,72,112,131,137,194,70,98,122,137,168,131,69,102,133,140,162,148,73,114,130,140,172,135,72,99,131,141,143,201,70,96,122,139,140,193,153,67
Data 107,133,140,138,195,85,80,112,134,141,139,184,98,74,118,132,139,138,175,169,64,100,132,140,141,135,189,135,67,108,132,138,139,136,176,159,62,99,129,138,139,138,142,184,105,71,114,137,140,141,135,135,167,131,67,107,128,140,138,136,136,133,172,109
Data 65,98,129,140,140,138,134,134,132,167,116,65,94,130,137,134,132,129,128,127,133,159,132,64,99,123,140,141,137,138,134,130,129,129,134,163,117,65,90,128,140,141,139,136,133,133,129,130,127,127,127,144,156,119,63,94,130,141,143,131,120,121,123,130
Data 115,113,115,119,121,113,112,114,130,119,118,118,139,127,125,126,139,122,124,121,132,118,121,115,140,126,124,126,129,118,118,114,124,122,136,127,121,120,128,124,115,132,119,146,118,125,118,143,125,147,126,136,120,145,116,126,125,121,131,112,132,113,157,124
Data 145,121,141,132,133,122,119,130,126,121,115,162,111,129,157,116,99,141,114,93,156,134,124,119,166,105,113,167,126,113,138,161,105,124,157,110,115,147,154,100,112,153,97,122,135,155,95,136,154,112,108,138,159,101,105,153,144,121,111,166,141,96,120,148
Data 139,98,115,161,131,109,124,158,151,95,137,155,143,105,109,170,150,98,108,137,169,107,97,134,155,134,96,119,159,150,111,108,133,173,129,95,126,132,166,116,93,132,148,143,96,98,129,154,143,118,96,128,158,150,112,104,129,153,154,111,108,125,142,162
Data 124,111,104,126,168,134,102,103,119,147,158,131,98,109,124,150,158,121,100,109,132,158,159,128,101,111,125,155,158,129,113,98,122,146,152,151,115,96,115,136,144,154,139,111,92,119,137,142,159,141,100,99,105,132,145,148,150,128,91,96,127,136,143,153
Data 146,111,95,104,127,139,149,152,148,128,92,92,123,137,139,149,156,141,110,90,99,123,138,140,144,147,145,116,96,98,115,135,138,141,147,147,150,123,94,93,106,129,140,142,139,147,150,139,126,94,94,109,133,142,144,141,142,149,144,135,114,100,90,102
Data 130,138,144,143,137,136,138,139,136,127,110,91,93,103,124,142,145,145,142,136,135,136,140,138,128,125,108,96,94,105,120,137,147,149,145,142,137,133,132,133,136,135,130,126,123,93,77,99,157,122,145,121,124,143,119,135,131,117,141,113,126,141,120,141
Data 115,121,124,117,141,119,119,130,115,138,122,132,131,116,140,112,134,128,117,136,114,138,119,134,125,117,144,115,144,114,122,116,120,145,111,145,111,132,127,131,128,117,147,116,146,110,130,127,131,126,118,147,119,147,112,138,113,141,115,125,113,127,129,118
Data 123,120,143,114,138,116,148,113,146,113,144,114,148,113,140,116,146,114,137,118,126,113,134,115,122,111,130,116,124,112,135,116,128,113,139,115,134,114,146,114,140,115,150,114,148,118,143,116,148,125,127,123,131,135,115,133,116,149,114,149,113,148,117,137
Data 118,136,132,117,133,115,148,113,149,113,125,125,121,145,113,148,113,132,121,125,125,114,148,113,149,121,122,127,116,150,114,147,116,135,135,115,145,114,133,123,120,132,114,148,117,132,139,114,151,114,120,132,116,148,119,126,129,117,149,117,127,128,114,148
Data 114,123,144,111,144,118,115,150,113,136,124,115,151,117,124,151,115,137,125,115,149,118,121,153,115,127,131,114,135,126,116,143,122,118,149,119,119,146,113,119,143,113,135,142,113,136,125,116,135,125,114,152,126,115,150,120,115,145,122,117,137,125,115,149
Data 131,113,139,123,113,129,133,114,122,144,113,125,154,115,119,154,122,116,148,121,113,128,135,113,128,153,114,120,155,125,115,138,129,113,123,153,114,116,140,128,114,123,156,113,117,131,130,111,124,153,117,116,138,148,112,122,154,132,113,125,154,122,115,130
Data 144,112,117,136,134,112,120,143,128,113,122,146,125,113,122,147,125,113,121,145,126,112,121,139,131,111,118,133,141,110,116,128,153,111,113,127,156,119,111,123,139,134,110,117,129,155,113,112,124,142,131,110,117,129,157,127,110,123,134,156,115,114,126,133
Data 152,111,117,125,137,146,109,113,127,137,142,113,114,126,136,147,115,113,127,133,153,122,110,124,130,142,137,106,111,125,130,154,124,107,121,130,133,155,110,113,124,132,139,145,112,111,126,132,135,155,111,112,123,131,134,156,113,107,121,131,133,139,145,106
Data 114,124,131,132,151,137,105,116,128,132,133,147,127,105,113,127,132,132,135,148,107,106,122,131,133,132,140,139,104,109,124,131,132,131,136,145,105,111,122,131,132,132,131,145,128,104,112,126,132,133,132,131,141,136,109,109,124,132,133,133,131,130,137,142
Data 106,111,122,130,133,132,132,130,130,142,130,106,109,125,131,131,130,129,128,128,127,138,135,110,106,122,131,134,133,132,131,130,128,128,129,137,139,107,108,119,130,133,133,133,132,130,129,128,127,127,127,133,139,129,104,109,121,131,134,135,128,136,126,131
Data 126,133,124,127,125,130,122,125,123,127,124,125,123,126,125,127,123,128,125,127,124,128,124,125,128,125,125,127,126,130,124,126,125,127,123,128,125,134,124,129,126,131,125,133,126,132,126,131,127,128,127,128,130,124,126,126,131,125,131,122,130,127,135,126
Data 131,127,131,129,128,130,123,129,128,129,118,137,123,118,133,127,113,124,144,112,133,144,122,121,144,134,113,142,132,117,125,144,119,112,140,127,119,134,141,116,117,143,131,120,128,142,122,115,139,127,121,120,142,121,114,132,139,122,117,131,141,114,120,136
Data 136,116,118,140,135,116,118,134,142,113,124,136,139,118,116,130,143,117,116,125,143,124,112,123,137,135,113,113,134,143,121,114,121,144,134,118,120,128,146,135,112,124,136,143,131,112,122,137,144,125,111,124,140,142,123,114,120,137,145,125,115,117,133,147
Data 138,114,115,125,137,140,120,113,116,130,145,134,118,114,122,137,147,135,114,115,121,136,146,132,120,113,123,139,145,140,123,110,121,132,140,143,127,112,113,129,135,141,146,123,108,117,126,135,145,142,126,111,112,123,134,139,141,134,113,108,122,129,135,141
Data 140,130,110,112,124,132,137,141,141,130,114,106,120,131,134,138,143,138,128,111,109,121,129,136,137,139,141,130,113,108,113,126,133,136,136,140,140,128,116,106,112,125,134,136,135,138,139,138,129,111,107,109,124,134,138,136,134,138,137,136,128,114,106,108
Data 120,132,137,137,135,133,134,135,133,129,120,111,107,111,123,131,137,138,137,133,133,132,135,134,131,127,118,110,107,111,120,131,136,139,138,137,135,133,130,131,132,132,130,127,126,118,101,107,124,134,135,125,133,127,126,132,123,129,131,122,129,122,126,132
Data 121,132,124,124,133,122,132,126,124,130,122,131,126,130,130,122,132,121,131,128,123,130,121,132,124,131,126,124,134,123,133,121,127,122,126,132,121,131,121,131,125,125,125,125,134,120,134,120,131,124,131,124,126,133,127,132,122,134,123,134,121,130,121,132
Data 123,127,122,128,129,124,126,126,133,122,135,124,135,122,134,123,135,122,134,123,135,121,133,121,133,119,130,120,134,120,133,122,135,121,134,123,135,122,136,125,134,123,136,127,129,126,131,130,124,129,125,128,121,133,121,132,122,136,122,136,126,129,125,128
Data 132,121,132,122,135,122,135,127,125,127,124,135,122,136,122,129,126,126,127,121,135,121,136,122,126,127,123,130,121,136,122,133,130,122,133,121,133,125,127,129,122,137,123,134,131,122,135,122,129,128,122,132,123,132,126,125,137,122,134,125,122,136,122,134
Data 124,120,135,121,131,132,121,137,122,122,135,122,135,131,122,138,123,122,136,122,134,132,122,137,124,122,138,123,128,136,122,131,128,122,134,127,122,137,125,122,137,121,122,136,121,128,136,121,128,130,121,126,131,121,126,133,121,132,135,121,129,130,121,125
Data 133,121,123,136,121,126,138,122,123,137,125,121,139,124,122,133,129,121,134,135,121,125,135,123,122,139,123,121,132,130,121,124,139,122,123,135,129,121,131,138,122,124,141,129,119,127,133,121,121,137,126,120,124,139,122,123,130,134,121,124,136,129,121,127
Data 140,125,121,130,141,123,122,132,139,122,122,134,138,121,123,135,138,121,122,133,138,121,122,131,140,121,121,129,141,124,120,126,141,127,119,125,139,135,119,123,131,141,122,120,127,136,130,119,124,128,140,121,120,125,135,132,119,121,127,141,125,119,123,129
Data 141,127,119,124,131,140,125,119,125,131,141,124,119,125,129,141,125,118,123,129,139,130,119,121,128,134,142,122,117,124,127,136,130,117,119,127,130,142,130,117,123,129,131,142,124,118,125,129,131,142,123,117,123,129,131,137,133,117,122,126,130,133,139,124
Data 116,124,129,130,134,136,117,117,124,129,131,133,139,118,118,123,128,130,131,139,126,116,122,129,130,131,133,136,122,116,123,129,130,130,131,138,125,115,122,128,130,130,130,135,137,118,117,125,129,130,130,130,131,138,121,116,121,128,130,131,130,129,132,137
Data 119,115,122,128,130,131,130,129,129,132,137,119,116,121,128,130,129,129,128,128,127,129,136,122,115,119,127,131,131,130,130,129,128,128,128,132,135,120,114,120,128,131,131,131,130,129,129,128,128,127,127,128,133,131,121,114,121,128,130,136,129,131,127,129
Data 127,130,127,129,126,129,127,129,126,128,126,129,126,127,126,128,126,128,126,129,126,128,126,129,125,127,126,128,126,129,126,130,126,128,127,128,126,130,127,129,127,129,128,127,127,129,128,126,128,127,127,126,129,126,128,126,129,127,129,127,130,128,130,128
Data 127,128,128,128,126,129,126,127,126,128,123,125,130,122,129,133,124,124,136,125,122,135,127,124,130,133,121,132,135,124,125,135,129,120,132,130,123,124,134,124,121,133,132,123,123,134,127,120,128,134,124,123,130,133,121,127,134,129,122,127,137,127,123,130
Data 133,128,122,131,133,125,122,129,136,126,120,130,135,128,121,126,136,129,122,125,134,135,122,123,131,135,126,119,128,135,130,122,123,128,137,125,122,125,134,134,121,123,127,135,132,120,120,131,137,129,121,121,132,136,132,122,122,130,137,133,122,121,126,136
Data 135,123,121,122,130,135,131,120,120,127,134,136,125,120,121,129,137,134,122,119,122,131,137,136,125,119,123,128,135,136,127,119,120,128,134,137,134,121,118,123,129,134,138,132,120,120,125,129,135,137,129,121,116,123,129,132,136,133,122,116,119,127,131,134
Data 136,131,120,118,121,127,132,135,136,133,122,116,118,126,131,133,135,136,130,120,116,119,127,132,132,134,136,131,125,116,118,125,130,133,133,134,135,132,123,116,117,122,129,132,132,133,134,134,130,123,116,116,123,128,133,133,132,133,134,133,129,124,117,115
Data 121,127,132,133,132,131,131,132,131,129,126,119,115,117,121,128,133,134,133,132,131,130,131,131,130,128,125,119,116,117,119,126,131,134,134,133,132,131,130,129,130,130,130,128,127,124,117,112,122,131,131,129,130,127,127,128,126,129,128,128,129,125,127,126
Data 126,129,125,129,127,126,129,125,129,127,126,128,125,129,127,128,128,125,129,125,129,127,125,128,125,129,126,127,127,126,130,124,130,125,128,128,127,129,125,130,125,129,126,127,126,127,129,125,129,125,129,125,130,125,128,128,128,128,126,130,126,130,125,130
Data 125,130,125,129,127,129,126,128,129,128,127,127,130,128,129,126,130,127,129,126,131,127,130,125,129,126,129,125,130,126,129,126,130,127,129,126,130,128,128,127,129,127,126,129,127,128,125,130,125,129,125,131,125,130,126,130,126,127,128,127,128,125,130,125
Data 130,125,130,126,130,128,126,128,125,130,125,130,125,131,127,127,128,126,130,125,131,125,127,128,126,129,125,131,125,130,128,125,130,125,130,126,128,127,125,131,125,131,128,125,130,125,130,127,127,129,125,131,126,128,128,125,132,126,129,130,125,131,126,125
Data 130,124,130,127,124,130,124,127,128,124,131,127,126,130,125,128,129,125,132,127,126,131,125,127,129,125,132,126,125,132,126,128,132,125,129,129,125,130,128,124,130,128,124,132,127,124,132,125,124,132,125,126,132,125,125,131,126,125,132,125,125,132,125,125
Data 132,126,125,132,125,124,132,126,124,130,128,124,131,130,124,128,130,125,125,132,125,124,132,127,124,129,130,124,128,133,125,125,133,128,124,130,130,125,125,133,126,124,129,130,124,126,132,126,123,130,131,124,125,133,129,124,127,132,126,124,130,130,125,125
Data 133,128,124,125,134,127,124,126,134,126,124,126,133,125,124,127,134,125,124,126,133,125,124,126,134,126,123,125,133,127,123,125,131,130,123,124,129,133,124,123,126,134,127,123,125,131,131,125,124,127,134,129,123,125,130,134,125,123,126,133,132,124,124,128
Data 133,130,123,125,127,134,128,123,124,128,134,128,123,124,127,134,132,123,124,128,130,134,125,123,126,129,135,127,122,123,127,131,134,124,122,126,129,132,131,123,124,126,129,135,128,123,124,127,129,133,131,123,124,126,129,132,132,125,122,126,128,129,134,128
Data 122,123,127,129,131,134,126,121,125,127,129,130,134,124,121,124,127,129,129,133,129,121,122,126,129,129,129,134,127,121,123,127,129,129,129,133,128,121,122,126,128,129,129,130,133,124,121,123,127,129,129,129,129,133,126,122,123,127,129,129,129,129,130,132
Data 128,121,123,126,129,129,129,129,129,130,133,128,121,123,127,128,129,128,128,128,128,128,132,129,121,122,126,129,129,129,129,129,128,128,128,129,132,128,121,122,125,129,130,130,129,129,128,128,128,128,127,128,129,131,126,120,121,126,129,132,130,128,128,128
Data 128,127,127,128,127,128,127,128,127,128,127,128,127,128,127,128,127,128,127,128,127,128,127,128,127,128,127,128,127,127,127,128,127,128,127,128,127,127,128,128,127,127,128,127,128,127,128,127,128,127,128,127,128,127,128,127,128,128,128,127,128,128,127,128
Data 127,129,127,128,127,128,128,127,127,127,127,126,129,126,126,130,127,125,129,128,124,130,130,126,128,131,127,126,131,127,125,129,129,124,126,131,126,125,129,130,124,129,131,126,125,130,130,124,126,131,128,125,128,131,125,124,130,129,125,125,131,129,125,126
Data 130,129,124,125,131,128,124,126,131,129,124,126,131,129,124,124,131,130,125,124,128,132,126,123,127,131,128,124,125,131,130,125,124,127,133,128,124,125,130,132,128,123,126,131,132,126,123,128,131,131,125,124,126,132,131,125,124,126,132,132,127,124,126,131
Data 133,129,124,124,126,132,130,124,123,125,130,133,128,124,123,127,132,133,128,123,124,127,131,133,128,124,124,127,131,133,130,124,123,125,129,132,132,128,123,124,128,130,132,131,125,122,124,128,131,133,131,125,121,124,126,130,132,131,128,122,122,126,128,130
Data 133,131,127,122,123,127,129,131,132,131,128,122,121,126,128,130,132,132,131,126,122,122,126,128,130,131,132,132,127,122,121,123,127,130,130,131,132,131,126,123,121,123,127,129,130,130,131,132,130,126,121,120,122,127,130,131,130,130,131,131,130,126,122,120
Data 121,126,129,131,131,130,130,130,130,129,127,124,121,120,123,127,129,131,131,131,130,129,130,130,130,128,127,123,122,121,122,126,128,131,132,131,131,130,129,129,129,129,129,128,127,127,122,119,123,128,128,129,129,128,129,128,127,128,127,128,128,127,128,126
Data 127,127,127,128,127,128,127,127,128,126,128,127,127,128,127,128,127,127,128,127,128,127,128,127,127,128,127,128,127,127,127,127,128,126,128,126,128,128,128,128,127,128,127,128,126,128,126,128,128,127,128,127,128,126,128,126,128,127,127,127,127,128,127,128
Data 127,128,126,128,126,128,127,129,127,128,127,128,127,128,128,128,127,128,128,128,127,128,128,128,127,127,127,127,127,127,128,127,127,128,128,127,127,128,127,128,127,128,127,128,127,129,127,128,127,129,127,128,127,128,127,127,128,127,128,127,129,127,128,127
Data 128,127,128,127,127,128,127,128,126,128,127,128,128,127,128,127,129,127,128,127,127,128,127,128,127,128,127,128,128,127,128,126,128,127,128,127,126,129,127,129,128,127,128,127,129,127,128,128,127,129,127,128,127,127,129,127,129,128,127,129,127,128,128,127
Data 128,127,127,128,126,128,127,127,128,127,128,128,127,129,127,127,128,127,128,128,127,129,127,127,128,127,127,128,127,129,128,127,129,127,127,129,127,128,129,127,128,128,127,128,128,126,128,128,126,129,128,126,129,127,126,129,128,127,129,128,127,129,127,126
Data 128,127,126,128,128,126,128,128,127,128,128,127,127,129,127,127,129,127,127,128,128,127,128,128,127,127,128,127,127,129,127,127,128,128,127,128,129,127,127,129,128,127,128,129,127,126,128,127,126,127,129,127,126,128,128,127,127,129,128,127,127,129,127,127
Data 128,129,127,127,128,128,127,127,128,128,127,127,129,128,127,127,129,128,127,127,129,128,127,127,129,128,127,127,128,129,127,127,128,129,127,127,127,129,128,127,127,128,128,127,127,127,129,127,127,127,128,128,127,127,127,129,127,127,127,128,129,127,127,127
Data 128,128,127,127,127,129,129,127,127,127,128,128,127,127,127,128,129,127,127,127,128,129,128,127,127,128,129,128,127,127,127,128,129,127,127,127,127,128,128,127,127,127,128,128,128,127,127,127,128,128,128,127,127,127,128,128,129,127,127,127,127,128,128,128
Data 127,127,127,128,128,128,128,127,127,127,128,128,128,128,127,127,127,128,128,128,128,127,127,127,127,128,128,128,128,127,127,127,128,128,128,128,128,127,127,127,127,128,128,128,128,128,127,127,127,128,128,128,128,128,128,127,127,127,128,128,128,128,128,128
Data 128,127,127,127,128,128,128,128,128,128,128,128,127,127,127,128,128,128,128,128,128,128,128,128,127,127,127,127,128,128,128,128,128,128,128,128,128,128,127,127,127,128,128,128,128,128,128,128,128,128,127,128,128,128,128,127,127,127,128,128,128,128,128,127
Data 128,127,128,128,128,128,128,128,128,0

.makeSound
	Restore shoot
	Read ln
	pos=0

	Repeat
		fileName$="C:\sound"+Rnd(1,1000)+".wav"
	Until FileType(fileName)=0

	fileOut=WriteFile(fileName)
		For pos=0 To ln-1
			Read bt
			WriteByte fileOut,bt
		Next
	CloseFile fileOut

	Global shootSound=LoadSound(fileName)
	DeleteFile fileName
Return
