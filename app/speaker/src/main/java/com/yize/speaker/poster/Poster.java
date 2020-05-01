package com.yize.speaker.poster;

import com.yize.speaker.Topic;

public interface Poster {
    void commit(Topic topic,Object msg);
}
