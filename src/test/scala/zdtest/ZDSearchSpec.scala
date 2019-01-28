package zdtest

import java.io.ByteArrayInputStream
import java.security.Permission

import org.scalacheck.Gen
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import zdtest.domain.ArbitraryInput
import zdtest.repo.Repository
import zdtest.search.Index

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class ZDSearchSpec(implicit ee: ExecutionEnv) extends Specification with ArbitraryInput {

  sequential

  private val repo = Repository(userList = Seq(genUser.sample.get))
  private val index = Await.result(repo.index, 10.seconds)
  private val ignore: String => Unit = _ => Unit

  "interactive user prompt loop" should {
    "allow user to quit" >> {
      val (i, o) = (new UserInput("quit"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, repo, index, o.write, ignore)) must beEqualTo(()).await
      o.results must beEmpty
    }

    "allow help command" >> {
      val (i, o) = (new UserInput("help", "q"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, repo, index, o.write, ignore)) must beEqualTo(()).await
      o.results mustEqual Seq(ZDSearch.helpMessage)
    }

    "allow help alias" >> {
      val (i, o) = (new UserInput("h", "q"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, repo, index, o.write, ignore)) must beEqualTo(()).await
      o.results mustEqual Seq(ZDSearch.helpMessage)
    }

    "allow fields command" >> {
      val (i, o) = (new UserInput("fields", "q"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, repo, index, o.write, ignore)) must beEqualTo(()).await
      o.results mustEqual Seq(ZDSearch.fieldsMessage)
    }

    "allow search command" >> {
      val (i, o) = (new UserInput(s"search user _id ${repo.users.keys.head}", "q"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, repo, index, o.write, ignore)) must beEqualTo(()).await
      o.results must not(beEmpty)
    }

    "never crash" >> prop { s: String =>
      val (i, o) = (new UserInput(s, "quit"), new CommandOutput)
      Future(ZDSearch.promptLoop(i.read, repo, index, o.write, ignore)) must beEqualTo(()).await
    }.setGen(Gen.identifier)
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
      val in = System.in
      System.setSecurityManager(new NoExitSecurityManager)
      try {
        val loop = Future(ZDSearch.main(Array("src/test/resources")))
        System.setIn(new ByteArrayInputStream(s"quit${System.lineSeparator()}".getBytes))
        loop must throwAn[ExitException](ExitException(0)).awaitFor(1.minute)
      } finally {
        System.setSecurityManager(null)
        System.setIn(in)
      }
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
