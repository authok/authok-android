package cn.authok.android.request.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonRequiredTypeAdapterFactoryTest {

    final String missingValue = "{}";
    Gson gson;

    @Before
    public void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new JsonRequiredTypeAdapterFactory())
                .create();
    }

    @Test
    public void shouldThrowExceptionIfMissingFieldIsRequired() {
        Exception error = null;
        try {
            gson.fromJson(missingValue, RequiredClass.class);
        } catch (Exception e) {
            error = e;
        }
        assertThat(error, notNullValue());
        assertThat(error, instanceOf(JsonParseException.class));
        assertThat(error.getMessage(), containsString("Missing required attribute value"));
    }

    @Test
    public void shouldNotThrowExceptionIfMissingFieldIsNotRequired() {
        NotRequiredClass bean = null;
        Exception error = null;
        try {
            bean = gson.fromJson(missingValue, NotRequiredClass.class);
        } catch (Exception e) {
            error = e;
        }
        assertThat(error, nullValue());
        assertThat(bean, notNullValue());
    }

    @SuppressWarnings("unused")
    static class RequiredClass {
        @JsonRequired
        Object value;
    }

    @SuppressWarnings("unused")
    static class NotRequiredClass {
        Object value;
    }
}