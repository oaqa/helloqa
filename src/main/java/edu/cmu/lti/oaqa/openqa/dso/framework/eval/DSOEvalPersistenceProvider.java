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

package edu.cmu.lti.oaqa.openqa.dso.framework.eval;

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

public class DSOEvalPersistenceProvider extends
		AbstractDSOMeasureEvalPersistenceProviderInterface {

	@Override
	public void deleteAggrEval(final Key key, final String sequenceId) {
		final String name = getClass().getSimpleName();
		String insert = getDeleteAggrEval();
		DataStoreImpl.getInstance().jdbcTemplate()
				.update(insert, new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, key.getExperiment());
						ps.setString(2, key.getTrace().getTraceHash());
						ps.setString(3, name);
						ps.setString(4, sequenceId);
					}
				});
	}

	@Override
	public void insertPartialCounts(final Key key, final String sequenceId,
			final DSOMeasureCounts counts) throws SQLException {
		final String eName = getClass().getSimpleName();
		String insert = getInsertAggregates();
		final Trace trace = key.getTrace();
		DataStoreImpl.getInstance().jdbcTemplate()
				.update(insert, new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, key.getExperiment());
						ps.setString(2, trace.getTrace());
						ps.setString(3, eName);
						ps.setFloat(4, counts.getAnsMRR());
						ps.setFloat(5, counts.getAnsAcc());
						ps.setFloat(6, counts.getsAnsRecall());
						ps.setFloat(7, counts.getsPassageRecall());
						ps.setFloat(8, counts.getAnsAcc());
						ps.setFloat(9, counts.getsIEErr());
						ps.setFloat(10, counts.getsPsgErr());
						ps.setFloat(11, counts.getCount());
						ps.setString(12, sequenceId);
						ps.setInt(13, key.getStage());
						ps.setString(14, trace.getTraceHash());
					}
				});
	}

	@Override
	public Multimap<Key, DSOMeasureCounts> retrievePartialCounts(
			final ExperimentKey experiment) {
		String select = getSelectAggregates();
		final Multimap<Key, DSOMeasureCounts> counts = LinkedHashMultimap
				.create();
		RowCallbackHandler handler = new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				Key key = new Key(rs.getString("experimentId"), new Trace(
						rs.getString("traceId")), rs.getInt("stage"));
				DSOMeasureCounts cnt = new DSOMeasureCounts(
						rs.getFloat("ans_mrr"), rs.getFloat("ans_acc"),
						rs.getFloat("ans_recall"),
						rs.getFloat("passage_recall"), rs.getFloat("ans_err"),
						rs.getFloat("ie_err"), rs.getFloat("psg_err"),
						rs.getInt("count"));
				counts.put(key, cnt);
			}
		};
		DataStoreImpl.getInstance().jdbcTemplate()
				.query(select, new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, experiment.getExperiment());
						ps.setInt(2, experiment.getStage());
					}
				}, handler);
		return counts;
	}

	@Override
	public void deleteMeasureEval(final ExperimentKey experiment) {
		String insert = getDeleteMeasureEval();
		DataStoreImpl.getInstance().jdbcTemplate()
				.update(insert, new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, experiment.getExperiment());
						ps.setInt(2, experiment.getStage());
					}
				});
	}

	@Override
	public void insertMeasureEval(final Key key, final String eName,
			final DSOEvaluationData eval) throws SQLException {
		String insert = getInsertMeasureEval();
		final Trace trace = key.getTrace();
		DataStoreImpl.getInstance().jdbcTemplate()
				.update(insert, new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, key.getExperiment());
						ps.setString(2, trace.getTrace());
						ps.setString(3, eName);
						ps.setFloat(4, eval.getAnsMRR());
						ps.setFloat(5, eval.getAnsAcc());
						ps.setFloat(6, eval.getAnsRecall());
						ps.setFloat(7, eval.getPassageRecall());
						ps.setFloat(8, eval.getsAnsErr());
						ps.setFloat(9, eval.getsIEErr());
						ps.setFloat(10, eval.getsPsgErr());
						ps.setFloat(11, eval.getCount());
						ps.setInt(12, key.getStage());
						ps.setString(13, trace.getTraceHash());
					}
				});
	}

	private String getInsertAggregates() {
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO dso_aggregates");
		query.append(" (experimentId, traceId, aggregator, ");
		query.append("ans_mrr, ans_acc, ans_recall, passage_recall, ans_err,ie_err,psg_err,count, sequenceId, stage,traceHash) ");
		query.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		return query.toString();
	}

	private String getDeleteAggrEval() {
		StringBuilder query = new StringBuilder();
		query.append("DELETE FROM dso_aggregates WHERE ");
		query.append(" experimentId = ? AND traceHash = ? AND aggregator = ? AND sequenceId = ?");
		return query.toString();
	}

	private String getSelectAggregates() {
		StringBuilder query = new StringBuilder();
		query.append("SELECT experimentId, traceId, ");
		query.append(" ans_mrr, ans_acc, ans_recall, passage_recall, ans_err,ie_err,psg_err,count, stage ");
		query.append(" FROM dso_aggregates WHERE experimentId = ? AND stage = ?");
		return query.toString();
	}

	private String getDeleteMeasureEval() {
		StringBuilder query = new StringBuilder();
		query.append("DELETE FROM dso_eval WHERE ");
		query.append(" experimentId = ? AND stage = ?");
		return query.toString();
	}

	private String getInsertMeasureEval() {
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO dso_eval");
		query.append(" (experimentId, traceId, evaluator, ");
		query.append(" ans_mrr, ans_acc, ans_recall, passage_recall, ans_err,ie_err,psg_err,count,stage,traceHash) ");
		query.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
		return query.toString();
	}

}
