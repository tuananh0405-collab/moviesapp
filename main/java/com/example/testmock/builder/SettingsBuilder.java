package com.example.testmock.builder;

public class SettingsBuilder {
    private String movieCategoryFilter = "popular";
    private String sortOption;
    private int pagesPerLoading = 1;

    public SettingsBuilder setMovieCategoryFilter(String movieCategoryFilter) {
        this.movieCategoryFilter = movieCategoryFilter;
        return this;
    }

    public SettingsBuilder setSortOption(String sortOption) {
        this.sortOption = sortOption;
        return this;
    }

    public SettingsBuilder setPagesPerLoading(int pagesPerLoading) {
        this.pagesPerLoading = pagesPerLoading;
        return this;
    }

    public String getMovieCategoryFilter() {
        return movieCategoryFilter;
    }

    public String getSortOption() {
        return sortOption;
    }

    public int getPagesPerLoading() {
        return pagesPerLoading;
    }

    public SettingsBuilder build() {
        // Return the builder itself after setting all values
        return this;
    }

    @Override
    public String toString() {
        return "SettingsBuilder{" +
                "movieCategoryFilter='" + movieCategoryFilter + '\'' +
                ", sortOption='" + sortOption + '\'' +
                ", pagesPerLoading=" + pagesPerLoading +
                '}';
    }
}

