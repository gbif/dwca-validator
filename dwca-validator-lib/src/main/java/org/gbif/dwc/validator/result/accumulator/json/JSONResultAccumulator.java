package org.gbif.dwc.validator.result.accumulator.json;

import org.gbif.dwc.validator.aggregation.AggregationResult;
import org.gbif.dwc.validator.exception.ResultAccumulationException;
import org.gbif.dwc.validator.result.ResultAccumulator;
import org.gbif.dwc.validator.result.validation.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * ResultAccumulator implementation saving results to JSON file(s).
 * 
 * @author cgendreau
 */
public class JSONResultAccumulator implements ResultAccumulator {

  private final static String ROOT_OBJECT_NAME = "validationResults";
  private final String resultFilePath;
  private ObjectMapper om;
  private JsonGenerator jGenerator = null;

  private final AtomicInteger count;

  public JSONResultAccumulator(String resultFilePath) {
    this.resultFilePath = resultFilePath;
    this.count = new AtomicInteger(0);
  }

  private void openJsonGenerator() throws IOException {
    JsonFactory jfactory = new JsonFactory();
    jGenerator = jfactory.createJsonGenerator(new File(resultFilePath), JsonEncoding.UTF8);
    jGenerator.writeStartObject();
    jGenerator.writeArrayFieldStart(ROOT_OBJECT_NAME);

    // init ObjectMapper and do not serialize empty values (including null)
    om = new ObjectMapper();
    om.setSerializationInclusion(Inclusion.NON_EMPTY);
  }

  @Override
  public boolean accumulate(ValidationResult result) throws ResultAccumulationException {
    // Do not record passed result
    if (result.passed()) {
      return true;
    }

    try {
      if (jGenerator == null) {
        openJsonGenerator();
      }

      om.writeValue(jGenerator, result);
      count.incrementAndGet();
    } catch (JsonGenerationException e) {
      throw new ResultAccumulationException(e);
    } catch (JsonMappingException e) {
      throw new ResultAccumulationException(e);
    } catch (IOException ioEx) {
      throw new ResultAccumulationException(ioEx);
    }

    return true;
  }

  @Override
  public boolean accumulate(AggregationResult<?> result) throws ResultAccumulationException {
    throw new ResultAccumulationException("This ResultAccumulator was not configured to record AggregationResult");
  }

  @Override
  public void close() throws ResultAccumulationException {
    if (jGenerator != null) {
      try {
        jGenerator.writeEndArray();
        jGenerator.writeEndObject();

        jGenerator.close();
      } catch (IOException ioEx) {
        throw new ResultAccumulationException(ioEx);
      }
    }
  }

  @Override
  public int getValidationResultCount() {
    return count.get();
  }

  @Override
  public int getAggregationResultCount() {
    return 0;
  }

}
