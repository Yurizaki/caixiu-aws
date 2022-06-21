package com.holmes.aws.vocabulary;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Vocabulary {

    public final static String PARTITION_KEY_NAME = "k_vocab_id";
    public final static String TABLE_NAME = "T_VOCABULARY";

    private String k_vocab_id;
    private String chinese;
    private String pinyin;
    private VocabularyCategory category;

    /**
     * Default constructor required for DynamoDbEnhancedClient operations.
     */
    public Vocabulary() { }

    public Vocabulary(String chinese, String pinyin) {
        this.k_vocab_id = chinese;
        this.chinese = chinese;
        this.pinyin = pinyin;
    }

    public Vocabulary(String chinese, String pinyin, VocabularyCategory category) {
        this(chinese, pinyin);
        this.category = category;
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

    public VocabularyCategory getCategory() {
        return category;
    }

    public void setCategory(VocabularyCategory category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "{" + this.getChinese() + ", " + this.getPinyin() + ", " + this.getCategory() + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() == Vocabulary.class) {
            Vocabulary vocabularyCompare = (Vocabulary) obj;

            return this.getChinese().equals(vocabularyCompare.getChinese())
                    && this.getPinyin().equals(vocabularyCompare.getPinyin())
                    && this.getCategory().equals(vocabularyCompare.getCategory());
        }
        return false;
    }
}
