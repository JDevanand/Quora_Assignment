package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name ="answer")
@NamedQueries(
        {
                @NamedQuery(name = "answerByQuestionId" , query = "select a from AnswerEntity a where a.question.uuid = :quuid"),
                @NamedQuery(name = "answerById" , query = "select a from AnswerEntity a where a.uuid = :uuid")
        }
)
public class AnswerEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name ="UUID")
    @Size(max=200)
    private String uuid;

    @Column(name = "ANS")
    @Size(max = 255)
    @NotNull
    private String answer;

    @Column(name="DATE")
    @NotNull
    private Date answerCreatedDate;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private QuestionEntity question;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getAnswerCreatedDate() {
        return answerCreatedDate;
    }

    public void setAnswerCreatedDate(Date answerCreatedDate) {
        this.answerCreatedDate = answerCreatedDate;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
