package com.lijun.dto;

import com.lijun.entity.Setmeal;
import com.lijun.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
