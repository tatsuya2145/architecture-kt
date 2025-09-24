package infrastructure

import org.ktorm.database.Database

val db = Database.connect(
    url = "jdbc:mysql://localhost:3306/testdb",
    driver = "com.mysql.cj.jdbc.Driver",
    user = "root",
    password = "password"
)
