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

    private String name="Тав";

    @ManyToOne
    @JoinColumn
    private User user;

    private short strength=10;
    private short dexterity=10;
    private short constitution=10;
    private short intellect=10;
    private short wisdom=10;
    private short charisma=10;



}