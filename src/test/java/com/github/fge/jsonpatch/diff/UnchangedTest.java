/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonpatch.diff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonpatch.*;
import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.json.Json;
import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public final class UnchangedTest {

    private static final ObjectMapper MAPPER = JacksonUtils.newMapper();
    private static final TypeReference<Map<JsonPointer, JsonNode>> TYPE_REF = new TypeReference<Map<JsonPointer, JsonNode>>() {};

    private final JsonNode testData;

    public UnchangedTest()
            throws IOException {
        final String resource = "/jsonpatch/diff/unchanged.json";
        testData = JsonLoader.fromResource(resource);
    }

    @DataProvider
    public Iterator<Object[]> getTestData()
            throws IOException {
        final List<Object[]> list = Lists.newArrayList();

        for (final JsonNode node : testData)
            list.add(new Object[]{node.get("first"), node.get("second"),
                    MAPPER.readValue(node.get("unchanged").traverse(), TYPE_REF)});

        return list.iterator();
    }

    @Test(dataProvider = "getTestData")
    public void computeUnchangedValuesWorks(final JsonNode first,
                                            final JsonNode second, final Map<JsonPointer, JsonNode> expected) {
        final Map<JsonPointer, JsonNode> actual
                = JsonDiff.getUnchangedValues(first, second);

        assertEquals(actual, expected);
    }

    @Test
    public void testcase2() throws JsonProcessingException, JsonPointerException {
        String json = "{\n" +
                "  \"name\": \"extra data\",\n" +
                "  \"salary\": 56000\n" +
                "}";
        JsonNode jsonNode1 = new ObjectMapper().readTree(json);
        JsonPointer jsonPointer = new JsonPointer("/name");

        DiffOperation diffOperation = new DiffOperation(DiffOperation.Type.ADD,jsonPointer,jsonNode1,jsonPointer,jsonNode1);
        System.out.println(""+diffOperation.getType());
        System.out.println(""+diffOperation.getFrom());
        System.out.println(""+diffOperation.getOldValue());
        System.out.println(""+diffOperation.getPath());
        System.out.println(""+diffOperation.getValue());

    }

    @Test
    public void testcase3() throws JsonProcessingException, JsonPatchException, JsonPointerException {
        String json = "{\n" +
                "  \"name\": \"extra data\",\n" +
                "  \"salary\": 56000\n" +
                "}";
        JsonPointer jsonPointer = new JsonPointer("/name");
        JsonNode jsonNode1 = new ObjectMapper().readTree(json);
        AddOperation addOperation = new AddOperation(jsonPointer, jsonNode1);
        System.out.println(""+addOperation.apply(jsonNode1));

    }


    @Test
    public void testcase4() throws JsonProcessingException, JsonPatchException, JsonPointerException {
        String json = "{\n" +
                "  \"name\": \"extra data\",\n" +
                "  \"salary\": 56000\n" +
                "}";
        JsonPointer jsonPointer = new JsonPointer("/name");
        JsonNode jsonNode1 = new ObjectMapper().readTree(json);

        Map<JsonPointer, JsonNode> map = new HashMap<>();
        map.put(jsonPointer, jsonNode1);
        DiffProcessor diffProcessor = new DiffProcessor(map);
        System.out.println(""+diffProcessor.getPatch());

        diffProcessor.valueAdded(jsonPointer,jsonNode1,jsonNode1);
        diffProcessor.valueRemoved(jsonPointer,jsonNode1,jsonNode1);
        diffProcessor.valueReplaced(jsonPointer,jsonNode1,jsonNode1,jsonNode1);
    }

    @Test
    public void testcase5() throws JsonProcessingException, JsonPatchException, JsonPointerException {
        String json = "{\n" +
                "  \"name\": \"extra data\",\n" +
                "  \"salary\": 56000\n" +
                "}";
        String json2 = "{\n" +
                "  \"name\": \"extra data\",\n" +
                "  \"salary\": 6000\n" +
                "}";

        JsonPointer jsonPointer = new JsonPointer("/name");
        JsonNode jsonNode1 = new ObjectMapper().readTree(json);
        JsonNode jsonNode2 = new ObjectMapper().readTree(json2);
        Map<JsonPointer, Set<String>> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        set.add("name");
        map.put(jsonPointer,set);

        JsonDiff.asJson(jsonNode1,jsonNode2,map);
        JsonDiff.asJsonPatch(jsonNode1,jsonNode2,map);
        JsonDiff.getUnchangedValues(jsonNode1,jsonNode2);

    }


    @Test
    public void testcase6() throws JsonProcessingException, JsonPatchException, JsonPointerException {
        String json = "{\n" +
                "  \"name\": \"extra data\",\n" +
                "  \"salary\": 56000\n" +
                "}";
        JsonPointer jsonPointer = new JsonPointer("/name");
        JsonNode jsonNode1 = new ObjectMapper().readTree(json);


        ReplaceOperation replaceOperation = new ReplaceOperation(jsonPointer, jsonNode1);
        replaceOperation.apply(jsonNode1);


    }

    @Test
    public void testcase7() throws JsonProcessingException, JsonPatchException, JsonPointerException {
        String json = "{\n" +
                "  \"name\": \"extra data\",\n" +
                "  \"salary\": 56000\n" +
                "}";
        JsonPointer jsonPointer = new JsonPointer("/name");
        JsonNode jsonNode1 = new ObjectMapper().readTree(json);


        AddOperation addOperation = new AddOperation(jsonPointer,jsonNode1);
        addOperation.apply(jsonNode1);

    }

    @Test
    public void testcase8() throws JsonProcessingException, JsonPatchException, JsonPointerException {
        String json = "{\n" +
                "  \"name\": \"extra data\",\n" +
                "  \"salary\": 56000\n" +
                "}";
        JsonPointer jsonPointer = new JsonPointer("/name");
        JsonNode jsonNode1 = new ObjectMapper().readTree(json);

        CopyOperation copyOperation = new CopyOperation(jsonPointer,jsonPointer,jsonNode1);
        copyOperation.apply(jsonNode1);


    }

    @Test
    public void testcase9() throws JsonProcessingException, JsonPatchException, JsonPointerException {
        String json = "{\n" +
                "  \"name\": \"extra data\",\n" +
                "  \"salary\": 56000\n" +
                "}";
        JsonPointer jsonPointer = new JsonPointer("/name");
        JsonNode jsonNode1 = new ObjectMapper().readTree(json);


        MoveOperation moveOperation = new MoveOperation(jsonPointer,jsonPointer,jsonNode1);
        moveOperation.apply(jsonNode1);
    }

    @Test
    public void testcase10() throws JsonProcessingException, JsonPatchException, JsonPointerException {
        String json = "{\n" +
                "  \"name\": \"extra data\",\n" +
                "  \"salary\": 56000\n" +
                "}";
        JsonPointer jsonPointer = new JsonPointer("/name");
        JsonNode jsonNode1 = new ObjectMapper().readTree(json);


        RemoveOperation removeOperation = new RemoveOperation(jsonPointer,jsonNode1);
        removeOperation.apply(jsonNode1);
        removeOperation.toString();

    }


    @Test
    public void testcase11() throws JsonProcessingException, JsonPatchException, JsonPointerException
    {
        String json = "{\n" +
                "  \"name\": \"extra data\",\n" +
                "  \"salary\": 56000\n" +
                "}";
        JsonPointer jsonPointer = new JsonPointer("/name");
        JsonNode jsonNode1 = new ObjectMapper().readTree(json);


        PathValueOperation pathValueOperation = new TestOperation(jsonPointer,jsonNode1);
        pathValueOperation.getValue();
        pathValueOperation.toString();

    }

    @Test
    public void testcase12()
    {
        JsonPatchException jsonPatchException  = new JsonPatchException("abc");
        JsonPatchException jsonPatchException2  = new JsonPatchException("abc",new Throwable());
        jsonPatchException.getMessage();
        jsonPatchException2.getMessage();
    }

    @Test
    public void testCase1() throws JsonProcessingException {

        //this is sequential data
        String json1 = "{\n" +
                "  \"Role Display Name\": \"Test Role Pset\",\n" +
                "  \"Role Category\": \"Default\",\n" +
                "  \"Role Name\": \"Test Role Pset\",\n" +
                "  \"Role Description\": \"Test Role Pset\",\n" +
                "  \"Organization\": \"Confluxsys\",\n" +
                "  \"Role Owner Login\": \"PBULE\",\n" +
                "  \"Role ID\": \"14350\",\n" +
                "  \"$identifier\": \"14350\",\n" +
                "  \"Entitlements\": [\n" +
                "    {\n" +
                "      \"Application Key\": \"121\",\n" +
                "      \"Entitlement Type\": \"UD_GROUPS_GROUPS\",\n" +
                "      \"Entitlement Name\": \"144~Network Security Role\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"Application Key\": \"562\",\n" +
                "      \"Entitlement Type\": \"UD_GROUPS_GROUPS\",\n" +
                "      \"Entitlement Name\": \"565~Deployment Owners\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"Application Key\": \"100\",\n" +
                "      \"Entitlement Type\": \"UD_GROUPS_GROUPS\",\n" +
                "      \"Entitlement Name\": \"565~Deployment Owners\",\n" +
                "      \"Entitlement Key\": \"askjdhfiegig02k\",\n" +
                "      \"Additional Info\": \"test\"\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";





        String json2 = "{\n" +
                "    \"Role Display Name\": \"Test Role Pset\",\n" +
                "    \"Role Category\": \"Default\",\n" +
                "    \"Role Name\": \"Test Role Pset\",\n" +
                "    \"Role Description\": \"Test Role Pset\",\n" +
                "    \"Organization\": \"Confluxsys\",\n" +
                "    \"Role Owner Login\": \"JDOE\",\n" +
                "    \"Role ID\": \"14350\",\n" +
                "    \"$identifier\": \"14350\",\n" +
                "    \"Entitlements\": [\n" +
                "      {\n" +
                "        \"Application Key\": \"122\",\n" +
                "        \"Entitlement Type\": \"UD_GROUPS_GROUPS\",\n" +
                "        \"Entitlement Name\": \"144~Network Security Role2\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"Application Key\": \"562\",\n" +
                "        \"Entitlement Type\": \"UD_GROUPS_GROUPS\",\n" +
                "        \"Entitlement Name\": \"565~Deployment Owners\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"Application Key\": \"100\",\n" +
                "        \"Entitlement Type\": \"UD_GROUPS_GROUPS\",\n" +
                "        \"Entitlement Name\": \"565~Deployment Owners\",\n" +
                "        \"Entitlement Key\": \"updated\",\n" +
                "        \"Additional Info\": \"test updated\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n";
/*

        String json3 = "{\n" +
                "  \"Role Display Name\": \"Test Role Pset\",\n" +
                "  \"Role Category\": \"Default\",\n" +
                "  \"Role Name\": \"Test Role Pset\",\n" +
                "  \"Role Description\": \"Test Role Pset\",\n" +
                "  \"Organization\": \"Confluxsys\",\n" +
                "  \"Role Owner Login\": \"JDOE\",\n" +
                "  \"Role ID\": \"14350\",\n" +
                "  \"$identifier\": \"14350\",\n" +
                "  \"Entitlements\": [\n" +
                "    {\n" +
                "      \"Application Key\": \"122\",\n" +
                "      \"Entitlement Type\": \"UD_GROUPS_GROUPS\",\n" +
                "      \"Entitlement Name\": \"144~Network Security Role2\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"Application Key\": \"562\",\n" +
                "      \"Entitlement Type\": \"UD_GROUPS_GROUPS\",\n" +
                "      \"Entitlement Name\": \"565~Deployment Owners\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"Application Key\": \"100\",\n" +
                "      \"Entitlement Type\": \"UD_GROUPS_GROUPS\",\n" +
                "      \"Entitlement Name\": \"565~Deployment Owners\",\n" +
                "      \"Entitlement Key\": \"updated\",\n" +
                "      \"Additional Info\": \"test updated\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"Application Key\": \"200\",\n" +
                "      \"Entitlement Type\": \"sarvesh\",\n" +
                "      \"Entitlement Name\": \"dhawale\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
*/


        JsonNode jsonNode1 = new ObjectMapper().readTree(json1);
        Assert.assertNotNull(jsonNode1);
        JsonNode jsonNode2 = new ObjectMapper().readTree(json2);
        Assert.assertNotNull(jsonNode2);

        JsonPointer pointer = JsonPointer.of("Entitlements");
        Map<JsonPointer, Set<String>> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        set.add("Application Key");
        set.add("Entitlement Type");
        set.add("Entitlement Name");
        map.put(pointer,set);


        /** Trail on jsonNode to check its keys and values */
       /* String jsonArray1 =jsonNode1.get("Entitlements").toString();
        String jsonArray2 =jsonNode2.get("Entitlements").toString();
        String jsonArray3 = jsonArray1.substring(1, jsonArray1.length() - 1);
        String jsonArray4 = jsonArray2.substring(1, jsonArray2.length() - 1);
        //convert string into map
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String > result1 = mapper.readValue(jsonArray3,  new TypeReference<Map<String, String>>(){});
        Map<String, String > result2 = mapper.readValue(jsonArray4, new TypeReference<Map<String, String>>(){});
        //here only one node is access
        compareJson(result1,result2);*/
        /** ends here */


        JsonNode jsonNode = JsonDiff.asJson(jsonNode1, jsonNode2, map);
        Assert.assertNotNull(jsonNode);
        System.out.println("" + jsonNode.toPrettyString());
    }


    public static void compareJson(Map<String,String> map1, Map<String,String> map2)
    {
        if (map1 == null && map2 == null)
        {
            System.out.println("either map 1 or map 2 is null");
        }

        assert map1 != null;

        if (map1.size() == map2.size())
        {
            if (map1.keySet().equals(map2.keySet()))
            {
                System.out.println("both node keys are same ");
            }
            if (map1.values().toString().equals(map2.values().toString()))
            {
                System.out.println("Both node values are same ");
                System.out.println("both node are same ");
            }else{
                System.out.println("op "+":"+" replace");
                System.out.println("original value : "+ map1);
            }

        }else {

            int a = map1.size();
            int b = map2.size();

            if ( a > b)
            {
                System.out.println("one key and value is extra or one key or value is less");
                System.out.println("op "+":"+" add");
                System.out.println("original value : "+ map1);
            }
            if (b > a)
            {
                System.out.println("one key and value is extra or one key or value is less");
                System.out.println("op "+":"+"add");
                System.out.println("original value : "+ map2);
            }

        }
    }
}


