# Sourceデータベース作成
$currentPath = $(get-location).Path


#su postgres \ su psql -Upostgres -Ppostgres -c "CREATE DATABASE input_data ;"

docker exec -it postgres-server su postgres  psql -c "DROP DATABASE IF EXISTS SourceDatabase ;CREATE DATABASE SourceDatabase ;"
 

 psql -U admin admin 


docker exec -it postgres-server psql -U postgres postgres << EOSQL 
  DROP DATABASE IF EXISTS SourceDatabase ;CREATE DATABASE SourceDatabase ; 
EOSQL



# SInkデータベース作成

