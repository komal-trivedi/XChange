package org.knowm.xchange.bitmex.dto.trade;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Value;
import org.knowm.xchange.dto.Order.IOrderFlags;

public enum BitmexOrderFlags implements IOrderFlags {
  FCIB, // prefer fee in base currency
  FCIQ, // prefer fee in quote currency
  NOMPP, // no market price protection
  POST, // for market maker orders
  VIQC; // volume in quote currency

  private static final Map<String, BitmexOrderFlags> fromString = new HashMap<>();

  static {
    for (BitmexOrderFlags orderFlag : values()) fromString.put(orderFlag.toString(), orderFlag);
  }

  public static BitmexOrderFlags fromString(String orderTypeString) {

    return fromString.get(orderTypeString.toLowerCase());
  }

  @Override
  public String toString() {

    return super.toString().toLowerCase();
  }

  static class BitmexOrderFlagsDeserializer extends JsonDeserializer<Set<BitmexOrderFlags>> {

    @Override
    public Set<BitmexOrderFlags> deserialize(JsonParser jsonParser, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {

      ObjectCodec oc = jsonParser.getCodec();
      JsonNode node = oc.readTree(jsonParser);
      String orderFlagsString = node.textValue();
      Set<BitmexOrderFlags> orderFlags = EnumSet.noneOf(BitmexOrderFlags.class);
      if (!orderFlagsString.isEmpty()) {
        for (String orderFlag : orderFlagsString.split(",")) orderFlags.add(fromString(orderFlag));
      }
      return orderFlags;
    }
  }

  public interface BinanceOrderFlags extends IOrderFlags {

    static BinanceOrderFlags withClientId(String clientId) {
      return new ClientIdFlag(clientId);
    }

    /** Used in fields 'newClientOrderId' */
    String getClientId();
  }

  @Value
  static final class ClientIdFlag implements BinanceOrderFlags {
    private final String clientId;
  }
}
