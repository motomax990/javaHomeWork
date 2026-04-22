package com.fractalflame.config;

import java.util.List;

public final class JsonConfig {

    public Size size;
    public Integer iteration_count;
    public String output_path;
    public Integer threads;
    public Double seed;
    public Integer symmetry_level;
    public List<FunctionCfg> functions;
    public List<AffineCfg> affine_params;

    public static final class Size {
        public Integer width;
        public Integer height;
    }

    public static final class FunctionCfg {
        public String name;
        public Double weight;
    }

    public static final class AffineCfg {
        public Double a, b, c, d, e, f;
    }
}
