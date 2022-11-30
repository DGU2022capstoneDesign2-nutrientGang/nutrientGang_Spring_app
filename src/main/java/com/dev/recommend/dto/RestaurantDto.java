package com.dev.recommend.dto;

import com.dev.register.dto.FoodDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto {
    private String rtrName;
    private String rtrImgURL;
    private String rtrLocation;
    private double rtrLat;
    private double rtrLng;
    private List<MenuDto> rtrMenu;

}