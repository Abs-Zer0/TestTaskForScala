package models

import models.PhoneBook.phonebook
import slick.jdbc.H2Profile.api._

object Main {
	val db = Database.forURL("jdbc:h2:tcp://localhost/~/test;DB_CLOSE_DELAY=-1", "admin", "admin", driver = "org.h2.Driver", executor = AsyncExecutor("test", numThreads = 10, queueSize = 1000))
	val setup = DBIO.seq(phonebook.schema.createIfNotExists)
	val action = db.run(setup)
}
