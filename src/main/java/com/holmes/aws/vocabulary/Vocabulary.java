package com.holmes.aws.vocabulary;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;

@DynamoDbBean
public class Vocabulary {

    String k_vocab_id;
    String chinese;
    String pinyin;

    public Vocabulary() {

    }

    public Vocabulary(String chinese, String pinyin) {
        this.chinese = chinese;
        this.pinyin = pinyin;
    }

    public Vocabulary(String k_vocab_id, String chinese, String pinyin) {
        this.k_vocab_id = k_vocab_id;
        this.chinese = chinese;
        this.pinyin = pinyin;
    }

    @DynamoDbPartitionKey
    public String getK_vocab_id() {
        return k_vocab_id;
    }

    public void setK_vocab_id(String k_vocab_id) {
        this.k_vocab_id = k_vocab_id;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public PutItemRequest getRequest(String table) {
        HashMap<String, AttributeValue> mappy = new HashMap<>();
        mappy.put("k_vocab_id", AttributeValue.builder().s(this.chinese).build());
        mappy.put("chinese", AttributeValue.builder().s(this.chinese).build());
        mappy.put("pinyin", AttributeValue.builder().s(this.pinyin).build());

        return PutItemRequest.builder()
                .tableName(table)
                .item(mappy)
                .build();
    }

    @Override
    public String toString() {
        return "{" + this.k_vocab_id + ", " + this.chinese + ", " + this.pinyin + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() == Vocabulary.class) {
            Vocabulary vocabularyCompare = (Vocabulary) obj;
            return this.chinese.equals(vocabularyCompare.chinese)
                    && this.pinyin.equals(vocabularyCompare.pinyin);
        }
        return false;
    }
}
