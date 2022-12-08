package com.dev.register.entity;

import com.dev.member.entity.Member;
import com.dev.utils.StringArrayConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lunch")
@NoArgsConstructor
@Getter
public class Lunch {
    @Id
    @Column(name = "lunch_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate date;

    @Convert(converter = StringArrayConverter.class)
    @Column
    private List<String> menu = new ArrayList<>();


    @Column
    private String imgUrl;
    @Convert(converter = StringArrayConverter.class)
    @Column
    private List<String> xmains = new ArrayList<>();
    @Convert(converter = StringArrayConverter.class)
    @Column
    private List<String> ymains = new ArrayList<>();

    @Convert(converter = StringArrayConverter.class)
    @Column
    private List<String> calories = new ArrayList<>();

    @Convert(converter = StringArrayConverter.class)
    @Column
    private List<String> carbohydrates = new ArrayList<>();

    @Convert(converter = StringArrayConverter.class)
    @Column
    private List<String> proteins = new ArrayList<>();

    @Convert(converter = StringArrayConverter.class)
    @Column
    private List<String> fats = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private Member member;

    public void setCalories(Integer calorie){
        String stringCalorie = String.valueOf(calorie);
        this.calories.add(stringCalorie);
    }


    public void setCarbohydrates(Integer carbohydrate){
        String stringCarbohydrate = String.valueOf(carbohydrate);
        this.carbohydrates.add(stringCarbohydrate);
    }

    public void setProteins(Integer protein){
        String stringProtein = String.valueOf(protein);
        this.proteins.add(stringProtein);
    }

    public void setFats(Integer fat){
        String stringFat = String.valueOf(fat);
        this.fats.add(stringFat);
    }


    public void setMenu(String menu){
        this.menu.add(menu);
    }

    public void setImgUrl(String imgUrl){
        if(imgUrl != null){
            this.imgUrl = imgUrl;
        }
    }

    public void setXmains(Double xmain){
        if(xmain!=null) {
            String stringXmain = String.valueOf(xmain);
            this.xmains.add(stringXmain);
        }else{
            this.xmains.add(" ");
        }
    }

    public void setYmains(Double ymain){
        if(ymain != null){
            String stringYmain = String.valueOf(ymain);
            this.ymains.add(stringYmain);
        }else{
            this.ymains.add(" ");
        }
    }

    public void setDate(LocalDate date){
        this.date = date;
    }

    public void setMember(Member member){
        if(this.member != null){
            this.member.getLunches().remove(this);
        }
        this.member = member;
        member.getLunches().add(this);
    }
}
