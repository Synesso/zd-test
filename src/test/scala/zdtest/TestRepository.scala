package zdtest

import java.io.File

import zdtest.repo.Repository
import zdtest.search.Index

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object TestRepository {

  lazy val (repo: Repository, index: Index) = Await.result(
    for {
      repo <- Repository.fromDir(new File("src/test/resources"))
      index <- repo.index
    } yield (repo, index), 5.seconds)
}
