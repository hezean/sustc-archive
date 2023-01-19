struct DayInfo {
    enum Day {
        Sunday,
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday
    };
    enum Weather { Sunny, Rainy, Cloudy, Snowy };

    Day d;
    Weather w;
};

bool canTravel(DayInfo);
