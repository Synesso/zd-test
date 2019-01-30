package zdtest

import java.security.Permission

import org.scalacheck.Gen
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import zdtest.TestRepository.{repo => testRepo, index}
import zdtest.domain.ArbitraryInput

import scala.concurrent.Future

class ZDSearchSpec(implicit ee: ExecutionEnv) extends Specification with ArbitraryInput {

  sequential

  private val ignore: String => Unit = _ => Unit

  "interactive user prompt loop" should {
    "allow user to quit" >> {
      val (i, o) = (new UserInput("quit"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, testRepo, index, o.write, ignore)) must beEqualTo(()).await
      o.results must beEmpty
    }

    "allow help command" >> {
      val (i, o) = (new UserInput("help", "q"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, testRepo, index, o.write, ignore)) must beEqualTo(()).await
      o.results mustEqual Seq(ZDSearch.helpMessage)
    }

    "allow help alias" >> {
      val (i, o) = (new UserInput("h", "q"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, testRepo, index, o.write, ignore)) must beEqualTo(()).await
      o.results mustEqual Seq(ZDSearch.helpMessage)
    }

    "allow fields command" >> {
      val (i, o) = (new UserInput("fields", "q"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, testRepo, index, o.write, ignore)) must beEqualTo(()).await
      o.results mustEqual Seq(ZDSearch.fieldsMessage)
    }

    "allow search command for users" >> {
      val (i, o) = (new UserInput(s"search user _id ${testRepo.users.keys.head}", "q"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, testRepo, index, o.write, ignore)) must beEqualTo(()).await
      o.results must not(beEmpty)
    }

    "allow search command for orgs" >> {
      val (i, o) = (new UserInput(s"search org _id ${testRepo.organisations.keys.head}", "q"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, testRepo, index, o.write, ignore)) must beEqualTo(()).await
      o.results must contain("1 result")
    }

    "allow search command for tickets" >> {
      val (i, o) = (new UserInput(s"search ticket _id ${testRepo.tickets.keys.head}", "q"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, testRepo, index, o.write, ignore)) must beEqualTo(()).await
      o.results must not(beEmpty)
    }

    "never crash" >> prop { s: String =>
      val (i, o) = (new UserInput(s, "quit"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, testRepo, index, o.write, ignore)) must beEqualTo(()).await
    }.setGen(Gen.identifier)

    "understand zero-text input" >> {
      val (i, o) = (new UserInput("", "", "", "", "", "", "q"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, testRepo, index, o.write, ignore)) must beEqualTo(()).await
      o.results must beEmpty
    }
  }

  "main method" should {
    "exit immediately when the data files are not present" >> {
      System.setSecurityManager(new NoExitSecurityManager)
      try {
        Future(ZDSearch.main(Array.empty[String])) must throwAn[ExitException](ExitException(-1)).await
      } finally {
        System.setSecurityManager(null)
      }
    }

    "process a user session when the repo can be loaded" >> {
      val loop = ZDSearch.startApp(new UserInput("q").read, Array("src/test/resources"))
      loop must not(throwAn[Exception]).await
    }
  }

  // models sequential user input for test purposes
  private class UserInput(xs: String*) {
    var next: Seq[String] = xs
    def read: String = {
      val h +: t = next
      next = t
      h
    }
  }

  // captures string output for later inspection
  private class CommandOutput {
    private val resultAccumulator = List.newBuilder[String]
    def results: Seq[String] = resultAccumulator.result()
    def write(s: String): Unit = resultAccumulator += s
  }

  private case class ExitException(status: Int) extends SecurityException("System.exit called")

  // traps exits in order to test exit code
  private class NoExitSecurityManager extends SecurityManager {
    override def checkPermission(perm: Permission): Unit = {}
    override def checkPermission(perm: Permission, context: Object): Unit = {}
    override def checkExit(status: Int): Unit = {
      super.checkExit(status)
      throw ExitException(status)
    }
  }
}
