package ru.baldursgate3.tgbot.bot.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table
public class GameCharacter {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn
    private User user;

    private short strength;
    private short dexterity;
    private short constitution;
    private short intellect;
    private short wisdom;
    private short charisma;



}