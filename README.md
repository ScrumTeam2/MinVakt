# MinVakt
Fiktivt system for vaktliste i helsetjenesten

# Kodestrukur
- Backend-kode: src/main/java/no/ntnu/stud/minvakt
- Backend-test: src/test/java/no/ntnu/stud/minvakt
- Frontend/web: src/main/webapp

# Kodekonvensjon
## Generelt
- All kode skal være på engelsk. Dette gjelder også kommentarer, git-commits og databasen

## Java
- Vi tar i bruk standard Java-navnekonvensjon: http://www.oracle.com/technetwork/java/codeconventions-135099.html

## Frontend
- Vi tar i bruk W3s praksis: 
- http://www.w3schools.com/js/js_conventions.asp
- http://www.w3schools.com/js/js_best_practices.asp
- http://www.w3schools.com/html/html5_syntax.asp

## Database
- Tabellnavn skal vanligvis være substantiv i entall, og bør være selvforklarende (F.eks. user og ikke users)
- Både kolonnenavn og tabellnavn skal være i lowercase, og ord skilles med underscore (\_) (F.eks. first_name og ikke FirstName eller firstname)
- Løpenummer-kolonner bør hete "id" (F.eks. user.id og ikke user.user_number)
