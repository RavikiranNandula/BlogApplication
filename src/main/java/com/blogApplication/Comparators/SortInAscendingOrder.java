package com.blogApplication.Comparators;

import com.blogApplication.model.Posts;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Date;

@Component
public class SortInAscendingOrder implements Comparator<Posts> {
    @Override
    public int compare(Posts o1, Posts o2) {
        Date publishedData=o1.getPublishedAt();
        Date anotherPublishedData=o2.getPublishedAt();
        if(publishedData!=null && anotherPublishedData!=null){
            return publishedData.compareTo(anotherPublishedData);
        }
        else {
            if(publishedData==null || anotherPublishedData==null){
                return 0;
            }
            else{
                return publishedData.compareTo(anotherPublishedData);
            }
        }
    }
}
