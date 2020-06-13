package com.leysoft.catssandra

import java.lang
import java.nio.ByteBuffer
import java.util.Optional
import java.util.concurrent.{CompletableFuture, CompletionStage}

import com.datastax.oss.driver.api.core.{CqlIdentifier, CqlSession, ProtocolVersion}
import com.datastax.oss.driver.api.core.`type`.DataType
import com.datastax.oss.driver.api.core.`type`.codec.registry.CodecRegistry
import com.datastax.oss.driver.api.core.`type`.reflect.GenericType
import com.datastax.oss.driver.api.core.context.DriverContext
import com.datastax.oss.driver.api.core.cql.{AsyncCqlSession, AsyncResultSet, ColumnDefinitions, ExecutionInfo, Row}
import com.datastax.oss.driver.api.core.data.GettableByName
import com.datastax.oss.driver.api.core.detach.AttachmentPoint
import com.datastax.oss.driver.api.core.metadata.Metadata
import com.datastax.oss.driver.api.core.metrics.Metrics
import com.datastax.oss.driver.api.core.session.Request
import com.leysoft.catssandra.connection.Session

package object interpreter {

  protected[interpreter] def sessionRec: Session =
    new Session {
      override def cql: CqlSession = new FakeRecCqlSession
    }

  protected[interpreter] def session: Session =
    new Session {
      override def cql: CqlSession = new FakeCqlSession
    }

  private final class FakeRow extends Row with GettableByName {

    override def getColumnDefinitions: ColumnDefinitions = ???

    override def firstIndexOf(id: CqlIdentifier): Int = ???

    override def getType(id: CqlIdentifier): DataType = ???

    override def firstIndexOf(name: String): Int = ???

    override def getType(name: String): DataType = ???

    override def getBytesUnsafe(i: Int): ByteBuffer = ???

    override def size(): Int = ???

    override def getType(i: Int): DataType = ???

    override def codecRegistry(): CodecRegistry = ???

    override def protocolVersion(): ProtocolVersion = ???

    override def isDetached: Boolean = ???

    override def attach(attachmentPoint: AttachmentPoint): Unit = ???

    override def getString(name: String): String = "test"
  }

  private final class FakeRecAsyncResultSet() extends AsyncResultSet {
    override def wasApplied(): Boolean = ???

    override def getColumnDefinitions: ColumnDefinitions = ???

    override def getExecutionInfo: ExecutionInfo = ???

    override def remaining(): Int = ???

    override def currentPage(): lang.Iterable[Row] =
      java.util.List.of(new FakeRow)

    override def hasMorePages: Boolean = true

    override def fetchNextPage(): CompletionStage[AsyncResultSet] =
      CompletableFuture.supplyAsync(() => new FakeAsyncResultSet)
  }

  private final class FakeAsyncResultSet() extends AsyncResultSet {
    override def wasApplied(): Boolean = ???

    override def getColumnDefinitions: ColumnDefinitions = ???

    override def getExecutionInfo: ExecutionInfo = ???

    override def remaining(): Int = ???

    override def currentPage(): lang.Iterable[Row] =
      java.util.List.of(new FakeRow)

    override def hasMorePages: Boolean = false

    override def fetchNextPage(): CompletionStage[AsyncResultSet] = ???
  }

  private final class FakeRecCqlSession
      extends CqlSession
      with AsyncCqlSession {

    override def executeAsync(query: String): CompletionStage[AsyncResultSet] =
      CompletableFuture.supplyAsync(() => new FakeRecAsyncResultSet)

    override def getName: String = ???

    override def getMetadata: Metadata = ???

    override def isSchemaMetadataEnabled: Boolean = ???

    override def setSchemaMetadataEnabled(
      newValue: lang.Boolean
    ): CompletionStage[Metadata] = ???

    override def refreshSchemaAsync(): CompletionStage[Metadata] = ???

    override def checkSchemaAgreementAsync(): CompletionStage[lang.Boolean] =
      ???

    override def getContext: DriverContext = ???

    override def getKeyspace: Optional[CqlIdentifier] = ???

    override def getMetrics: Optional[Metrics] = ???

    override def execute[RequestT <: Request, ResultT](
      request: RequestT,
      resultType: GenericType[ResultT]
    ): ResultT = ???

    override def closeFuture(): CompletionStage[Void] = ???

    override def closeAsync(): CompletionStage[Void] = ???

    override def forceCloseAsync(): CompletionStage[Void] = ???
  }

  private final class FakeCqlSession extends CqlSession with AsyncCqlSession {

    override def executeAsync(query: String): CompletionStage[AsyncResultSet] =
      CompletableFuture.supplyAsync(() => new FakeAsyncResultSet)

    override def getName: String = ???

    override def getMetadata: Metadata = ???

    override def isSchemaMetadataEnabled: Boolean = ???

    override def setSchemaMetadataEnabled(
      newValue: lang.Boolean
    ): CompletionStage[Metadata] = ???

    override def refreshSchemaAsync(): CompletionStage[Metadata] = ???

    override def checkSchemaAgreementAsync(): CompletionStage[lang.Boolean] =
      ???

    override def getContext: DriverContext = ???

    override def getKeyspace: Optional[CqlIdentifier] = ???

    override def getMetrics: Optional[Metrics] = ???

    override def execute[RequestT <: Request, ResultT](
      request: RequestT,
      resultType: GenericType[ResultT]
    ): ResultT = ???

    override def closeFuture(): CompletionStage[Void] = ???

    override def closeAsync(): CompletionStage[Void] = ???

    override def forceCloseAsync(): CompletionStage[Void] = ???
  }
}
