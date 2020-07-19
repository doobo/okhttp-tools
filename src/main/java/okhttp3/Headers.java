package okhttp3;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpDate;

public final class Headers {
    private final String[] namesAndValues;

    Headers(Headers.Builder builder) {
        this.namesAndValues = (String[])builder.namesAndValues.toArray(new String[builder.namesAndValues.size()]);
    }

    private Headers(String[] namesAndValues) {
        this.namesAndValues = namesAndValues;
    }

    @Nullable
    public String get(String name) {
        return get(this.namesAndValues, name);
    }

    @Nullable
    public Date getDate(String name) {
        String value = this.get(name);
        return value != null ? HttpDate.parse(value) : null;
    }

    @Nullable
    public Instant getInstant(String name) {
        Date value = this.getDate(name);
        return value != null ? value.toInstant() : null;
    }

    public int size() {
        return this.namesAndValues.length / 2;
    }

    public String name(int index) {
        return this.namesAndValues[index * 2];
    }

    public String value(int index) {
        return this.namesAndValues[index * 2 + 1];
    }

    public Set<String> names() {
        TreeSet<String> result = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        int i = 0;

        for(int size = this.size(); i < size; ++i) {
            result.add(this.name(i));
        }

        return Collections.unmodifiableSet(result);
    }

    public List<String> values(String name) {
        List<String> result = null;
        int i = 0;

        for(int size = this.size(); i < size; ++i) {
            if (name.equalsIgnoreCase(this.name(i))) {
                if (result == null) {
                    result = new ArrayList(2);
                }

                result.add(this.value(i));
            }
        }

        return result != null ? Collections.unmodifiableList(result) : Collections.emptyList();
    }

    public long byteCount() {
        long result = (long)(this.namesAndValues.length * 2);
        int i = 0;

        for(int size = this.namesAndValues.length; i < size; ++i) {
            result += (long)this.namesAndValues[i].length();
        }

        return result;
    }

    public Headers.Builder newBuilder() {
        Headers.Builder result = new Headers.Builder();
        Collections.addAll(result.namesAndValues, this.namesAndValues);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        return other instanceof Headers && Arrays.equals(((Headers)other).namesAndValues, this.namesAndValues);
    }

    public int hashCode() {
        return Arrays.hashCode(this.namesAndValues);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        int i = 0;

        for(int size = this.size(); i < size; ++i) {
            result.append(this.name(i)).append(": ").append(this.value(i)).append("\n");
        }

        return result.toString();
    }

    public Map<String, List<String>> toMultimap() {
        Map<String, List<String>> result = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        int i = 0;

        for(int size = this.size(); i < size; ++i) {
            String name = this.name(i).toLowerCase(Locale.US);
            List<String> values = (List)result.get(name);
            if (values == null) {
                values = new ArrayList(2);
                result.put(name, values);
            }

            ((List)values).add(this.value(i));
        }

        return result;
    }

    @Nullable
    private static String get(String[] namesAndValues, String name) {
        for(int i = namesAndValues.length - 2; i >= 0; i -= 2) {
            if (name.equalsIgnoreCase(namesAndValues[i])) {
                return namesAndValues[i + 1];
            }
        }

        return null;
    }

    public static Headers of(String... namesAndValues) {
        if (namesAndValues == null) {
            throw new NullPointerException("namesAndValues == null");
        } else if (namesAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Expected alternating header names and values");
        } else {
            namesAndValues = (String[])namesAndValues.clone();

            int i;
            for(i = 0; i < namesAndValues.length; ++i) {
                if (namesAndValues[i] == null) {
                    throw new IllegalArgumentException("Headers cannot be null");
                }

                namesAndValues[i] = namesAndValues[i].trim();
            }

            for(i = 0; i < namesAndValues.length; i += 2) {
                String name = namesAndValues[i];
                String value = namesAndValues[i + 1];
                checkName(name);
                checkValue(value, name);
            }

            return new Headers(namesAndValues);
        }
    }

    public static Headers of(Map<String, String> headers) {
        if (headers == null) {
            throw new NullPointerException("headers == null");
        } else {
            String[] namesAndValues = new String[headers.size() * 2];
            int i = 0;

            for(Iterator var3 = headers.entrySet().iterator(); var3.hasNext(); i += 2) {
                Entry<String, String> header = (Entry)var3.next();
                if (header.getKey() == null || header.getValue() == null) {
                    throw new IllegalArgumentException("Headers cannot be null");
                }

                String name = ((String)header.getKey()).trim();
                String value = ((String)header.getValue()).trim();
                checkName(name);
                checkValue(value, name);
                namesAndValues[i] = name;
                namesAndValues[i + 1] = value;
            }

            return new Headers(namesAndValues);
        }
    }

    static void checkName(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        } else if (name.isEmpty()) {
            throw new IllegalArgumentException("name is empty");
        } else {
            int i = 0;

            for(int length = name.length(); i < length; ++i) {
                char c = name.charAt(i);
                if (c <= ' ' || c >= 127) {
                    throw new IllegalArgumentException(Util.format("Unexpected char %#04x at %d in header name: %s", new Object[]{Integer.valueOf(c), i, name}));
                }
            }

        }
    }

    static void checkValue(String value, String name) {
        if (value == null) {
            throw new NullPointerException("value for name " + name + " == null");
        }
    }

    public static final class Builder {
        final List<String> namesAndValues = new ArrayList(20);

        public Builder() {
        }

        Headers.Builder addLenient(String line) {
            int index = line.indexOf(":", 1);
            if (index != -1) {
                return this.addLenient(line.substring(0, index), line.substring(index + 1));
            } else {
                return line.startsWith(":") ? this.addLenient("", line.substring(1)) : this.addLenient("", line);
            }
        }

        public Headers.Builder add(String line) {
            int index = line.indexOf(":");
            if (index == -1) {
                throw new IllegalArgumentException("Unexpected header: " + line);
            } else {
                return this.add(line.substring(0, index).trim(), line.substring(index + 1));
            }
        }

        public Headers.Builder add(String name, String value) {
            Headers.checkName(name);
            Headers.checkValue(value, name);
            return this.addLenient(name, value);
        }

        public Headers.Builder addUnsafeNonAscii(String name, String value) {
            Headers.checkName(name);
            return this.addLenient(name, value);
        }

        public Headers.Builder addAll(Headers headers) {
            int i = 0;

            for(int size = headers.size(); i < size; ++i) {
                this.addLenient(headers.name(i), headers.value(i));
            }

            return this;
        }

        public Headers.Builder add(String name, Date value) {
            if (value == null) {
                throw new NullPointerException("value for name " + name + " == null");
            } else {
                this.add(name, HttpDate.format(value));
                return this;
            }
        }

        public Headers.Builder add(String name, Instant value) {
            if (value == null) {
                throw new NullPointerException("value for name " + name + " == null");
            } else {
                return this.add(name, new Date(value.toEpochMilli()));
            }
        }

        public Headers.Builder set(String name, Date value) {
            if (value == null) {
                throw new NullPointerException("value for name " + name + " == null");
            } else {
                this.set(name, HttpDate.format(value));
                return this;
            }
        }

        public Headers.Builder set(String name, Instant value) {
            if (value == null) {
                throw new NullPointerException("value for name " + name + " == null");
            } else {
                return this.set(name, new Date(value.toEpochMilli()));
            }
        }

        Headers.Builder addLenient(String name, String value) {
            this.namesAndValues.add(name);
            this.namesAndValues.add(value.trim());
            return this;
        }

        public Headers.Builder removeAll(String name) {
            for(int i = 0; i < this.namesAndValues.size(); i += 2) {
                if (name.equalsIgnoreCase((String)this.namesAndValues.get(i))) {
                    this.namesAndValues.remove(i);
                    this.namesAndValues.remove(i);
                    i -= 2;
                }
            }

            return this;
        }

        public Headers.Builder set(String name, String value) {
            Headers.checkName(name);
            Headers.checkValue(value, name);
            this.removeAll(name);
            this.addLenient(name, value);
            return this;
        }

        @Nullable
        public String get(String name) {
            for(int i = this.namesAndValues.size() - 2; i >= 0; i -= 2) {
                if (name.equalsIgnoreCase((String)this.namesAndValues.get(i))) {
                    return (String)this.namesAndValues.get(i + 1);
                }
            }

            return null;
        }

        public Headers build() {
            return new Headers(this);
        }
    }
}