package com.example.quiz.model.dto;

public class QuizModifierEffectDto {
    private String id; // this is is set by the subclasses and serves as identifier of the class
    private String name;
    private Integer duration;
    private String description;
	private String type;


	private Boolean isPermanent;
	private Integer rarity;

    public QuizModifierEffectDto(String id, String name, Integer duration, String description, String type, Boolean isPermanent, Integer rarity) {
        this.setId(id);
        this.setName(name);
        this.setDuration(duration);
        this.setDescription(description);
		this.setType(type);
		this.setPermanent(isPermanent);
		this.setRarity(rarity);
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

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPermanent(Boolean permanent) {
		isPermanent = permanent;
	}

	public void setRarity(Integer rarity) {
		this.rarity = rarity;
	}
	public Boolean getPermanent() {
		return isPermanent;
	}

	public String getType() {
		return type;
	}

	public Integer getRarity() {
		return rarity;
	}



}