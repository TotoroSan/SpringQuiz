package com.example.quiz.model.dto;


public class QuizModifierEffectDto {
    private String id; // this is is set by the subclasses and serves as identifier of the class
    private String name;
    private int duration;
    private String description;

    public QuizModifierEffectDto(String id, String name, int duration, String description) {
        this.setId(id);
        this.setName(name);
        this.setDuration(duration);
        this.setDescription(description);
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}



}