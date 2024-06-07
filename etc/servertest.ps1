Invoke-WebRequest -Uri http://127.0.0.1:8080/lobbies/newlobby -Method POST  -ContentType application/json -Body '{"rounds": 11, "board_map_id": 0}'
