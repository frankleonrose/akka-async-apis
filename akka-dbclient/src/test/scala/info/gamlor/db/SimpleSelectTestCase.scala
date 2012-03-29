package info.gamlor.db

import akka.dispatch.Await
import akka.util.duration._
import org.adbcj.ConnectionManagerProvider

/**
 * @author roman.stoffel@gamlor.info
 * @since 29.03.12
 */

class SimpleSelectTestCase extends SpecBaseWithH2{


  describe("Basic DB operations") {
    it("can get connection "){

      val connection = Database(system).connect()
      connection must not be(null)

      val result = Await.result(connection, 5 seconds)
      connection must not be(result)
    }
    it("fail to get connection is reported in future"){
      val notExistingServer: String = "adbcj:jdbc:h2:tcp://not.existing.localhost:8084/~/sample"
      val noExistingConnection = ConnectionManagerProvider.createConnectionManager(notExistingServer,"sa","")
      val connection = new DatabaseAccess(noExistingConnection,system.dispatcher).connect()
      connection must not be(null)



      val result = Await.ready(connection, 60 seconds)
      result.value.get.isLeft must be (true)
    }
    it("can select 1"){
      var selectedOne = for{
        connection <-Database(system).connect()
        r <- connection.executeQuery("SELECT 1 As count")

      } yield r.get(0).get(0).getInt;


      val result = Await.result(selectedOne, 5 seconds)
      result must be (1)
    }
  }



}
