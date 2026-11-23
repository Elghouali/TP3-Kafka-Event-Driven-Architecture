# PowerShell â available on Windows without curl alias ambiguity.
Invoke-RestMethod -Uri http://localhost:8081/api/sales -Method POST -ContentType 'application/json' -Body '{"product":"Laptop","region":"Casablanca","quantity":2,"unitPrice":6500.00}'
Invoke-RestMethod -Uri http://localhost:8082/api/consumer/stats
Invoke-RestMethod -Uri http://localhost:8080/api/analytics
Start-Process http://localhost:8080
