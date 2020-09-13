package com.changgou.service.impl;

import com.changgou.dao.BrandMapper;
import com.changgou.goods.pojo.Brand;
import com.changgou.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    @Override
    public Brand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo<Brand> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<Brand>(brandMapper.selectAll());
    }

    @Override
    public PageInfo<Brand> findPage(Brand brand, int page, int size) {
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(brand);
        //执行搜索
        return new PageInfo<Brand>(brandMapper.selectByExample(example));
    }

    private Example createExample(Brand brand) {
        Example example = new Example(Brand.class);//select * from tb_brand
        Example.Criteria criteria = example.createCriteria();
        //2.判断 拼接条件
        if (brand != null) {
            if (!StringUtils.isEmpty(brand.getName())) {// where name like ?
                //第一个参数:指定要条件比较的 属性的名称(POJO的属性名)
                //第二个参数:指定要比较的值
                criteria.andLike("name", "%" + brand.getName() + "%");
            }

            if (!StringUtils.isEmpty(brand.getLetter())) {// where letter = ?
                //第一个参数:指定要条件比较的 属性的名称(POJO的属性名)
                //第二个参数:指定要比较的值
                criteria.andEqualTo("letter", brand.getLetter());
            }
        }
        return example;
    }
}
