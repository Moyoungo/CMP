package com.dse.cmp.cmp;

/** 唯一指代某个数据集：数据源ID + 数据集名（如 "A","cat"） */
public record DatasetRef(String sid, String ds) { }
