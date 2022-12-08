package com.dev.recommend.service;


import com.dev.health.dto.*;
import com.dev.health.entity.HealthStatus;
import com.dev.health.repository.HealthStatusRepository;
import com.dev.health.repository.NutrientStatusRepository;
import com.dev.jwt.utils.SecurityUtil;
import com.dev.register.dto.FoodDto;
import com.dev.register.dto.FoodListDto;
import com.dev.register.entity.Food;
import com.dev.register.repository.FoodRepository;
import com.dev.utils.response.BaseException;
import com.dev.utils.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static com.dev.utils.response.BaseResponseStatus.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendFoodService {
    private final HealthStatusRepository healthStatusRepository;
    private final NutrientStatusRepository nutrientStatusRepository;
    private final FoodRepository foodRepository;

    @Transactional
    public CalorieInfoResDto getCalorieInfo(LocalDate date){
        Long userId = SecurityUtil.getCurrentMemberId();
        Integer needCalorie = healthStatusRepository.findNeedCalorieByMemberAndDate(userId, date);
        if (needCalorie == null) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_HEALTH_STATUS);
        }

        Integer haveCalorie = nutrientStatusRepository.findHaveCalorieByMemberAndDate(userId, date);
        if (haveCalorie == null) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_NUTRIENT_STATUS);
        }

        return CalorieInfoResDto.builder()
                .needCalorie(needCalorie)
                .haveCalorie(haveCalorie)
                .build();
    }

    @Transactional
    public NutrientStatusInfoResDto getNutrientInfo(LocalDate date){
        Long userId = SecurityUtil.getCurrentMemberId();

        // 섭취해야할 영양소 계산
        Optional<NutrientDto> needNutrientInfo = healthStatusRepository.findNeedNutrientByMemberAndDate(userId, date);
        if(needNutrientInfo.isEmpty()){
            throw new BaseException(BaseResponseStatus.NOT_FOUND_HEALTH_STATUS);
        }
        NutrientDto needNutrient = needNutrientInfo.get();


        // 섭취한 영양소 값
        Optional<NutrientDto> haveNutrientInfo = nutrientStatusRepository.findHaveNutrientInfoByMemberAndDate(userId, date);
        if(haveNutrientInfo.isEmpty()){
            throw new BaseException(BaseResponseStatus.NOT_FOUND_NUTRIENT_STATUS);
        }
        NutrientDto haveNutrient = haveNutrientInfo.get();

        return NutrientStatusInfoResDto.builder()
                .needNutrient(needNutrient)
                .haveNutrient(haveNutrient)
                .build();

    }

    private Optional<HealthStatus> getMemberType(LocalDate date){
        Long userId = SecurityUtil.getCurrentMemberId();
        return healthStatusRepository.findTargetByMember_IdAndDate(userId,date);
    }

    @Transactional
    public FoodListDto getRecommendBreakfast(LocalDate date){
//        CalorieInfoResDto calorieInfo = getCalorieInfo(date);
//
//        Double minBreakfastNeedCalorie = (double)calorieInfo.getNeedCalorie()/3-1000;
//        Double maxBreakfastNeedCalorie = (double)calorieInfo.getNeedCalorie()/3+1000;

        //Optional<List<Food>> foods = foodRepository.findByCalorieBetween(minBreakfastNeedCalorie, maxBreakfastNeedCalorie);
        Optional<List<Food>> foods = breakfastRecommendAlgorithm(date);
        if(foods.get().isEmpty()){
            throw new BaseException(BaseResponseStatus.NOT_FOUND_FOOD);
        }
        List<Food> fiveFoods = getRandomFiveFoods(foods);
        log.info("{}",fiveFoods.size());
        List<FoodDto> fiveFoodDtos = new ArrayList<>();
        for(Food f: fiveFoods){
            NutrientDto nutrient = new NutrientDto((int)f.getCarbohydrate(),
                    (int)f.getProtein(),
                    (int)f.getFat());
            FoodDto foodDto = FoodDto.builder()
                    .name(f.getName())
                    .calorie((int)f.getCalorie())
                    .nutrient(nutrient)
                    .build();
            fiveFoodDtos.add(foodDto);

        }
        log.info("{}",fiveFoodDtos.size());
        return FoodListDto.builder()
                .foodDtoList(fiveFoodDtos)
                .build();

    }

    @Transactional
    public FoodListDto getRecommendLunch(LocalDate date){

        Optional<List<Food>> foods = lunchRecommendAlgorithm(date);
        //log.info("{}",foods.get().get(0).getName());
        if(foods.get().isEmpty()){
            throw new BaseException(BaseResponseStatus.NOT_FOUND_FOOD);
        }
        List<Food> fiveFoods = getRandomFiveFoods(foods);
        log.info("{}",fiveFoods.size());
        List<FoodDto> fiveFoodDtos = new ArrayList<>();
        for(Food f: fiveFoods){
            NutrientDto nutrient = new NutrientDto((int)f.getCarbohydrate(),
                    (int)f.getProtein(),
                    (int)f.getFat());
            FoodDto foodDto = FoodDto.builder()
                    .name(f.getName())
                    .calorie((int)f.getCalorie())
                    .nutrient(nutrient)
                    .build();
            fiveFoodDtos.add(foodDto);

        }
        log.info("{}",fiveFoodDtos.size());
        return FoodListDto.builder()
                .foodDtoList(fiveFoodDtos)
                .build();

    }

    @Transactional
    public FoodListDto getRecommendDinner(LocalDate date){

        Optional<List<Food>> foods = dinnerRecommendAlgorithm(date);
        //log.info("{}",foods.get().get(0).getName());
        if(foods.get().isEmpty()){
            throw new BaseException(BaseResponseStatus.NOT_FOUND_FOOD);
        }
        List<Food> fiveFoods = getRandomFiveFoods(foods);
        log.info("{}",fiveFoods.size());
        List<FoodDto> fiveFoodDtos = new ArrayList<>();
        for(Food f: fiveFoods){
            NutrientDto nutrient = new NutrientDto((int)f.getCarbohydrate(),
                    (int)f.getProtein(),
                    (int)f.getFat());
            FoodDto foodDto = FoodDto.builder()
                    .name(f.getName())
                    .calorie((int)f.getCalorie())
                    .nutrient(nutrient)
                    .build();
            fiveFoodDtos.add(foodDto);

        }
        log.info("{}",fiveFoodDtos.size());
        return FoodListDto.builder()
                .foodDtoList(fiveFoodDtos)
                .build();

    }
    private Optional<List<Food>> breakfastRecommendAlgorithm(LocalDate date){
        Optional<HealthStatus> type = getMemberType(date);
        if(type.isEmpty()){
            throw new BaseException(NOT_FOUND_USER);
        }
        CalorieInfoResDto calorieInfo = getCalorieInfo(date);
        int needCalorie = calorieInfo.getNeedCalorie();
        log.info("{}",type.get().getTarget().name());
        if(type.get().getTarget().name()=="GAIN_MUSCLE"){
            Double minCalorie = (double)needCalorie/3-500;
            Double maxCalorie = (double)needCalorie/3+300;

            return foodRepository.findByCalorieBetweenAndMealTimeContaining(minCalorie,maxCalorie,"breakfast");
        }

        Double maxCalorie = (double)needCalorie/3;
        return foodRepository.findByCalorieLessThanAndMealTimeContaining(maxCalorie, "breakfast");

    }

    private Optional<List<Food>> lunchRecommendAlgorithm(LocalDate date){

        Optional<HealthStatus> type = getMemberType(date);
        if(type.isEmpty()){
            throw new BaseException(NOT_FOUND_USER);
        }

        CalorieInfoResDto calorieInfo = getCalorieInfo(date);
        NutrientStatusInfoResDto nutrientInfo = getNutrientInfo(date);
        NutrientDto needNutrient = nutrientInfo.getNeedNutrient();
        NutrientDto haveNutrient = nutrientInfo.getHaveNutrient();
        int needCalorie = calorieInfo.getNeedCalorie();
        int haveCalorie = calorieInfo.getHaveCalorie();
        if(type.get().getTarget().name().equals("GAIN_MUSCLE")) {
            if (haveCalorie == 0) {
                Double minCalorie = (double) needCalorie / 3 - 300;
                Double maxCalorie = (double) needCalorie / 3 + 300;
                return foodRepository.findByCalorieBetweenAndMealTimeContaining(minCalorie, maxCalorie, "lunch");
            }
            double minFoodCal = (double)(needCalorie-haveCalorie)/2-300;
            double maxFoodCal = (double)(needCalorie-haveCalorie)/2+300;

            return foodRepository.findByCalorieBetweenAndMealTimeContaining(minFoodCal,maxFoodCal,"lunch");
        }
        if(haveCalorie == 0){
            Double maxCalorie = (double)needCalorie/3;

            return foodRepository.findByCalorieLessThanAndMealTimeContaining(maxCalorie, "lunch");
        }
        double foodCal = (double)(needCalorie-haveCalorie)/2;
        double foodCarbo = (double)(needNutrient.getCarbohydrate()-haveNutrient.getCarbohydrate())/2;
        double foodPro = (double)(needNutrient.getProtein()-haveNutrient.getProtein())/2;
        double foodFat = (double)(needNutrient.getFat()-haveNutrient.getFat())/2;

        log.info("{} {}",foodCal,foodCarbo);

        return foodRepository.findByCalorieLessThanAndCarbohydrateLessThanAndProteinLessThanAndFatLessThanAndMealTimeContaining(foodCal,foodCarbo,foodPro,foodFat, "lunch");
    }
    private Optional<List<Food>> dinnerRecommendAlgorithm(LocalDate date){
        CalorieInfoResDto calorieInfo = getCalorieInfo(date);
        NutrientStatusInfoResDto nutrientInfo = getNutrientInfo(date);
        NutrientDto needNutrient = nutrientInfo.getNeedNutrient();
        NutrientDto haveNutrient = nutrientInfo.getHaveNutrient();
        int needCalorie = calorieInfo.getNeedCalorie();
        int haveCalorie = calorieInfo.getHaveCalorie();
        log.info("{}",haveCalorie);
        if(haveCalorie == 0){
            Double maxCalorie = (double)needCalorie/2;

            return foodRepository.findByCalorieLessThanAndMealTimeContaining(maxCalorie, "dinner");
        }
        double foodCal = needCalorie-haveCalorie;
        double foodCarbo = needNutrient.getCarbohydrate()-haveNutrient.getCarbohydrate()+10;
        double foodPro = needNutrient.getProtein()-haveNutrient.getProtein()+3;
        double foodFat = needNutrient.getFat()-haveNutrient.getFat()+3;

        log.info("{} {}",foodCal,foodCarbo);

        return foodRepository.findByCalorieLessThanAndCarbohydrateLessThanAndProteinLessThanAndFatLessThanAndMealTimeContaining(foodCal,foodCarbo,foodPro,foodFat, "dinner");
    }

    private List<Food> getRandomFiveFoods(Optional<List<Food>> foods){
        if(foods.get().size()<=5){
            return foods.get();
        }

        List<Food> fiveFoods = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        while (set.size() < 5) {
            Double d = Math.random() * foods.get().size();
            set.add(d.intValue());
        }
        for(int i : set){
            fiveFoods.add(foods.get().get(i));
        }
        return fiveFoods;
    }

}
