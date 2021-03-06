package com.neurchi.advisor.common.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.domain.model.Identity;
import com.neurchi.advisor.common.serializer.AbstractSerializer;

import java.io.IOException;
import java.time.Instant;

public class NotificationSerializer extends AbstractSerializer {

    private static NotificationSerializer notificationSerializer;
    private static NotificationSerializer notificationXmlSerializer;

    public static NotificationSerializer instance() {
        if (NotificationSerializer.notificationSerializer == null) {
            NotificationSerializer.notificationSerializer = new NotificationSerializer();
        }
        return NotificationSerializer.notificationSerializer;
    }

    public static NotificationSerializer xmlInstance() {
        if (NotificationSerializer.notificationXmlSerializer == null) {
            NotificationSerializer.notificationXmlSerializer = new NotificationSerializer(true);
        }
        return NotificationSerializer.notificationXmlSerializer;
    }

    public String serialize(final Notification notification) {
        try {
            return this.objectMapper().writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize notification.", e);
        }
    }

    private NotificationSerializer(final boolean isCompact, final boolean isPretty, final boolean isXml) {
        super(isCompact, isPretty, isXml);
    }

    private NotificationSerializer(final boolean isXml) {
        super(false, true, isXml);
    }

    private NotificationSerializer() {
        super(false, false);
    }

    @Override
    public void setupModule(final SetupContext context) {
        if (context.getOwner() instanceof XmlMapper) {
            // We might use a different representation for the Xml published language.
            SimpleSerializers simpleSerializers = new SimpleSerializers();
            simpleSerializers.addSerializer(new NotificationStdSerializer());
            context.addSerializers(simpleSerializers);

            SimpleDeserializers simpleDeserializers = new SimpleDeserializers();
            simpleDeserializers.addDeserializer(Notification.class, new NotificationStdDeserializer());
            context.addDeserializers(simpleDeserializers);
        }

        context.setMixInAnnotations(Identity.class, IdentityMixIn.class);
        context.setMixInAnnotations(DomainEvent.class, DomainEventMixIn.class);
    }

    private interface IdentityMixIn {
        @JsonValue
        String id();
    }

    private interface DomainEventMixIn {
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        int eventVersion();
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Instant occurredOn();
    }

    private static class NotificationStdSerializer extends StdSerializer<Notification> {

        protected NotificationStdSerializer() {
            super(Notification.class);
        }

        @Override
        public void serialize(final Notification value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
            final String eventType = value.typeName().substring(value.typeName().lastIndexOf('.') + 1);
            if (gen instanceof ToXmlGenerator generator) {
                generator.writeStartObject();
                generator.setNextIsAttribute(true);
                generator.writeNumberField("id", value.notificationId());
                generator.writeStringField("type", value.typeName());
                generator.writeObjectField("occurredOn", value.occurredOn());
                generator.writeNumberField("version", value.version());
                {
                    generator.setNextIsAttribute(false);
                    generator.writeFieldName(eventType);
                    generator.writeObject(value.event());
                }
                generator.writeEndObject();
            }
        }
    }

    private static class NotificationStdDeserializer extends StdDeserializer<Notification> {

        protected NotificationStdDeserializer() {
            super(Notification.class);
        }

        @Override
        public Notification deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return null;
        }
    }
}
