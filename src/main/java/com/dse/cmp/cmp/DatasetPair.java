package com.dse.cmp.cmp;

/** 显式点名要比较的“数据集对”（例如只比较 A.cat vs C.cat） */
public record DatasetPair(DatasetRef a, DatasetRef b) { }
