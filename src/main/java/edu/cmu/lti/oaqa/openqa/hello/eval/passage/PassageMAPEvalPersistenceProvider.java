/*
 *  Copyright 2012 Carnegie Mellon University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package edu.cmu.lti.oaqa.openqa.hello.eval.passage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import edu.cmu.lti.oaqa.ecd.phase.Trace;
import edu.cmu.lti.oaqa.framework.DataStoreImpl;
import edu.cmu.lti.oaqa.framework.eval.ExperimentKey;
import edu.cmu.lti.oaqa.framework.eval.Key;
import edu.cmu.lti.oaqa.framework.eval.passage.PassageMAPCounts;
import edu.cmu.lti.oaqa.framework.eval.passage.PassageMAPEvaluationData;
import edu.cmu.lti.oaqa.framework.persistence.AbstractPassageMAPEvalPersistenceProvider;

public class PassageMAPEvalPersistenceProvider extends AbstractPassageMAPEvalPersistenceProvider {

  @Override
  public void deletePassageAggrEval(final Key key, final String sequenceId) {
    final String name = getClass().getSimpleName();
    String insert = getDeletePassageAggrEval();
    DataStoreImpl.getInstance().jdbcTemplate().update(insert, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, key.getExperiment());
        ps.setString(2, key.getTrace().getTraceHash());
        ps.setString(3, name);
        ps.setString(4, sequenceId);
      }
    });
  }

  @Override
  public void insertPartialCounts(final Key key, final String sequenceId, final PassageMAPCounts counts)
          throws SQLException {
    final String eName = getClass().getSimpleName();
    String insert = getInsertPassageAggregates();
    final Trace trace = key.getTrace();
    DataStoreImpl.getInstance().jdbcTemplate().update(insert, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, key.getExperiment());
        ps.setString(2, trace.getTrace());
        ps.setString(3, eName);
        ps.setFloat(4, counts.getDocavep());
        ps.setFloat(5, counts.getPsgavep());
        ps.setFloat(6, counts.getAspavep());
        ps.setFloat(7, counts.getCount());
        ps.setString(8, sequenceId);
        ps.setInt(9, key.getStage());
        ps.setString(10, trace.getTraceHash());
      }
    });
  }

  @Override
  public Multimap<Key, PassageMAPCounts> retrievePartialCounts(final ExperimentKey experiment) {
    String select = getSelectPassageAggregates();
    final Multimap<Key, PassageMAPCounts> counts = LinkedHashMultimap.create();
    RowCallbackHandler handler = new RowCallbackHandler() {
      public void processRow(ResultSet rs) throws SQLException {
        Key key = new Key(rs.getString("experimentId"), new Trace(rs.getString("traceId")), rs.getInt("stage"));
        PassageMAPCounts cnt = new PassageMAPCounts(rs.getFloat("docavep"), rs.getFloat("psgavep"),
                rs.getFloat("aspavep"), rs.getInt("count"));
        counts.put(key, cnt);
      }
    };
    DataStoreImpl.getInstance().jdbcTemplate().query(select, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, experiment.getExperiment());
        ps.setInt(2, experiment.getStage());
      }
    }, handler);
    return counts;
  }
 
  @Override
  public void deletePassageMeasureEval(final ExperimentKey experiment) {
    String insert = getDeletePassageMeasureEval();
    DataStoreImpl.getInstance().jdbcTemplate().update(insert, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, experiment.getExperiment());
        ps.setInt(2, experiment.getStage());
      }
    });
  }

  @Override
  public void insertMAPMeasureEval(final Key key, final String eName, final PassageMAPEvaluationData eval)
          throws SQLException {
    String insert = getInsertMAPMeasureEval();
    final Trace trace = key.getTrace();
    DataStoreImpl.getInstance().jdbcTemplate().update(insert, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, key.getExperiment());
        ps.setString(2, trace.getTrace());
        ps.setString(3, eName);
        ps.setFloat(4, eval.getDocMap());
        ps.setFloat(5, eval.getPsgMap());
        ps.setFloat(6, eval.getAspMap());
        ps.setFloat(7, eval.getCount());
        ps.setInt(8, key.getStage());
        ps.setString(9, trace.getTraceHash());
      }
    });
  }
  
  private String getInsertPassageAggregates() {
    StringBuilder query = new StringBuilder();
    query.append("INSERT INTO map_aggregates");
    query.append(" (experimentId, traceId, aggregator, ");
    query.append("docavep, psgavep, aspavep, count, sequenceId, stage,traceHash) ");
    query.append(" VALUES (?,?,?,?,?,?,?,?,?,?)");
    return query.toString();
  }

  private String getDeletePassageAggrEval() {
    StringBuilder query = new StringBuilder();
    query.append("DELETE FROM map_aggregates WHERE ");
    query.append(" experimentId = ? AND traceHash = ? AND aggregator = ? AND sequenceId = ?");
    return query.toString();
  }
  
  private String getSelectPassageAggregates() {
    StringBuilder query = new StringBuilder();
    query.append("SELECT experimentId, traceId, ");
    query.append(" docavep, psgavep, aspavep, count, stage ");
    query.append(" FROM map_aggregates WHERE experimentId = ? AND stage = ?");
    return query.toString();
  }

  private String getDeletePassageMeasureEval() {
    StringBuilder query = new StringBuilder();
    query.append("DELETE FROM map_eval WHERE ");
    query.append(" experimentId = ? AND stage = ?");
    return query.toString();
  }

  private String getInsertMAPMeasureEval() {
    StringBuilder query = new StringBuilder();
    query.append("INSERT INTO map_eval");
    query.append(" (experimentId, traceId, evaluator, ");
    query.append(" docmap, psgmap, aspmap,count,stage,traceHash) ");
    query.append(" VALUES (?,?,?,?,?,?,?,?,?)");
    return query.toString();
  }

}
