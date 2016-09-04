package com.intelliq.appengine.stuff;

import java.util.Arrays;
import java.util.Random;

public class FakeData {

    public static final String[] names = {"Adam Ashton", "Adria Amor", "Aja Adelson", "Alecia Agarwal", "Alissa Ackman", "Allie Abila", "Anastasia Abarca", "Arlene Alba", "Arminda Acuff",
            "Audria Aderholt", "Bernadine Bucholtz", "Brittney Bruning", "Cameron Coonrod", "Carmine Caul", "Carmon Cabral", "Chantell Council", "Charlette Corsi", "Charlie Castaldi", "Daren Dorman",
            "Darren Difranco", "Delbert Dray", "Della Donaldson", "Dot Deaner", "Douglas Donnell", "Edra Emanuel", "Edyth Elsasser", "Elyse Ecklund", "Emelina Eagar", "Erlinda Eccles",
            "Ernest Ebert", "Eun Estridge", "Fatimah Fujiwara", "Floy Foulk", "Frances Finnegan", "Freeda Frink", "Georgeann Gwozdz", "Geraldo Goulette", "Hanh Holiday", "Hyacinth Hallinan",
            "Iliana Ismail", "Inger Icenhour", "Isiah Imes", "Jacklyn Johansen", "Jackson Jarnagin", "Jadwiga Jelinek", "Jake Jo", "Jeffery Jacobi", "Jesenia Jock", "Joella Jaworski",
            "Jonas Jiminez", "Jonell Jone", "Julietta Jordon", "Karren Kong", "Katharine Kothari", "Katia Kubala", "Ken Koonce", "Kristel Kerr", "Lashaunda Longfellow", "Lawrence Lumpkin",
            "Leeann Legette", "Lorilee Lovick", "Loris Landers", "Louis Laroche", "Lucie Leighton", "Lucille Lague", "Luetta Ligon", "Magnolia Mirsky", "Majorie Mcgrory", "Mandi Maynor",
            "Marianne Mauck", "Maribel Morison", "Maryanne Mirabella", "Mervin Miers", "Miranda Mclin", "Mireille Marson", "Mohammad Maharaj", "Myron Munday", "Nanette Necaise", "Nikole Nightingale",
            "Noelle Newhard", "Norah Nogle", "Qiana Quinlan", "Ramonita Rye", "Reinaldo Rochin", "Rhonda Rittenhouse", "Rolf Rickenbacker", "Sabra Stelter", "Samira Schildgen", "Sanford Strozier",
            "Sara Stahler", "Shan Storms", "Shannon Salmons", "Shon Stennett", "Tashia Troutt", "Terresa Tapley", "Toby Trottier", "Tommy Tookes", "Trinity Terrel", "Wilbert Winner", "Yang Yutzy"};

    public static String[] getNames(int count) {
        String[] randomNames = names;
        shuffleArray(randomNames);
        return Arrays.copyOfRange(randomNames, 0, count < 100 ? count : 99);
    }

    static void shuffleArray(String[] ar) {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            String a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

}
