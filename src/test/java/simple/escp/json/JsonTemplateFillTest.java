/*
 * Copyright 2014 Jocki Hendry
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package simple.escp.json;

import org.junit.Before;
import org.junit.Test;
import simple.escp.util.EscpUtil;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import static simple.escp.util.EscpUtil.*;

public class JsonTemplateFillTest {

    private String jsonString;
    private final String INIT = EscpUtil.escInitalize();

    @Before
    public void setup() {
        this.jsonString = "{" +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"nickname\"" +
            "]," +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
    }

    @Test
    public void fillMap() {
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Map<String, String> dataSource = new HashMap<>();
        dataSource.put("id", "007");
        dataSource.put("nickname", "Solid Snake");
        assertEquals(INIT + "Your id is 007, Mr. Solid Snake." + CRLF + CRFF + INIT, jsonTemplate.fill(dataSource));
    }

    @Test
    public void fillObject() throws Exception {
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Person person = new Person();
        person.setId("007");
        person.setNickname("Solid Snake");
        assertEquals(INIT + "Your id is 007, Mr. Solid Snake." + CRLF + CRFF + INIT, jsonTemplate.fill(person));
    }

    public static class Person {
        private String id;
        private String nickname;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

}
