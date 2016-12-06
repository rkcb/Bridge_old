package com.bridge.ui.club;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

import org.pegdown.PegDownProcessor;

import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.HtmlItem;
import com.bridge.ui.BridgeUI;
import com.vaadin.server.VaadinService;

public class ResourceManager {

    /***
     * basePath gives the base path for storing files
     */

    public static String basePath() {
        return VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
    }

    /***
     * clubNewsPath gives the news home directory for the club and returns null
     * if the id does not belong to a club
     */

    public static String clubNewsPath(Object clubEntityId) {
        C<Club> cs = new C<>(Club.class);
        if (clubEntityId != null && cs.c().containsId(clubEntityId)) {
            String b = basePath();
            String path = b + "/WEB-INF/clubNews/" + clubEntityId.toString()
                    + "/";
            return path;
        }
        return null;
    }

    /***
     * markdownFileNames returns a set of used file names in resources
     */

    public static HashSet<String> markdownFileNames(Object htmlId,
            PegDownProcessor p) {
        HashSet<String> fs = new HashSet<>();

        return fs;
    }

    /***
     * newsItemPath forms the resource path for the item of the club; returns
     * null if the club id or news id is invalid
     */

    public static String newsItemPath(Object clubId, Object htmlId) {
        String p = clubNewsPath(clubId);
        C<HtmlItem> is = new C<>(HtmlItem.class);
        if (p != null && htmlId != null && is.c().containsId(htmlId)) {
            return p + htmlId.toString() + "/";
        }
        return null;
    }

    /***
     * newsItemPath forms the resource path by the "owner" club in the item
     */

    public static String newsItemPath(Object newsId) {
        C<HtmlItem> c = new C<>(HtmlItem.class);

        if (newsId != null && c.c().containsId(newsId)) {
            HtmlItem i = c.get(newsId);
            if (i.getClub().getId() != null) {
                return newsItemPath(i.getClub().getId(), newsId);
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    /***
     * removeNewsFileResources removes all associated resource files of the news
     */

    public static void removeNewsFileResources(Object htmlId) {
        C<HtmlItem> htmls = new C<>(HtmlItem.class);
        Object clubId = htmls.get(htmlId).getClub().getId();

        if (clubId != null && htmlId != null) {
            String s = newsItemPath(htmlId);
            Path dir = null;
            try {
                dir = Paths.get(s);
            } catch (InvalidPathException e) {
            }

            File f = null;
            if (dir != null) {
                f = dir.toFile();
            }
            if (f != null && f.isDirectory()) {

                // removes the news directory and the containing files
                try (DirectoryStream<Path> stream = Files
                        .newDirectoryStream(dir)) {
                    for (Path file : stream) {
                        Files.walkFileTree(file, new FileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir,
                                    BasicFileAttributes attrs)
                                    throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path file,
                                    BasicFileAttributes attrs)
                                    throws IOException {
                                Files.delete(file);
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFileFailed(Path file,
                                    IOException exc) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir,
                                    IOException exc) throws IOException {
                                Files.delete(dir);
                                return FileVisitResult.CONTINUE;
                            }
                        });

                    } // for
                } // try
                catch (IOException e) {
                    e.printStackTrace();
                }
                if (f != null) {
                    f.delete();
                }

                Path club = Paths.get(clubNewsPath(clubId));
                if (club != null) {
                    File ff = club.toFile();
                    if (ff.isDirectory() && ff.listFiles().length == 0) {
                        ff.delete();
                    }
                }
            }
        }
    }

    /***
     * getNewsDirectory returns the file resource directory and creates it if
     * necessary; all file resources of this news are stored in this directory.
     * In invalid cases null is returned
     */

    public static String getNewsDirectory(Object htmlId) {
        C<HtmlItem> is = new C<>(HtmlItem.class);
        if (is.c().containsId(htmlId) && is.get(htmlId).getClub() != null) {
            return getNewsDirectory(is.get(htmlId).getClub().getId(), htmlId);
        } else {
            return null;
        }
    }

    /***
     * getNewsDirectory returns the file resource directory and creates it if
     * necessary; all file resources of this news are stored in this directory
     */

    public static String getNewsDirectory(Object clubEntityId, Object htmlId) {
        String path = newsItemPath(clubEntityId, htmlId);
        if (path != null) {
            Path p = Paths.get(path);
            File f = new File(path);
            if (!f.exists()) {
                try {
                    // create all needed nonexisting directories
                    Files.createDirectories(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                return path;
            }
        }
        return path;
    }

    /***
     * cleanResources removes file resources which are not referred by any news
     * according to markdown stored in the DB and then updates the news
     * accordingly
     */

    public static void cleanResources(Object htmlId, HashSet<String> found) {
        C<HtmlItem> is = new C<>(HtmlItem.class);
        Set<String> uploaded = is.get(htmlId).getFileNames();
        uploaded.removeAll(found);
        for (String s : uploaded) {
            File f = new File(getNewsDirectory(htmlId) + s);
            BridgeUI.o("delete file: " + f.getName());
            f.delete();
        }
        is.set(htmlId, "fileNames", found);
    }

    /***
     * findDirFiles returns all normal files' names in this directory
     */

    public static HashSet<String> findDirFiles(Path p) throws IOException {
        HashSet<String> found = new HashSet<>();
        try (DirectoryStream<Path> s = Files.newDirectoryStream(p)) {
            for (Path file : s) {
                if (file.toFile().isFile()) {
                    found.add(file.toFile().getName());
                }
            }
        }
        return found;
    }

    /***
     * clean removes recursively html item directories which are not used
     */

    public static void clean(Object clubEntityId) throws IOException {

        if (clubEntityId != null) {
            C<HtmlItem> c = new C<>(HtmlItem.class);
            Path dir = Paths.get(clubNewsPath(clubEntityId));
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path file : stream) {
                    Long htmlItemId = Long
                            .parseLong(file.getFileName().toString());
                    if (!c.c().containsId(htmlItemId)) {
                        Files.walkFileTree(file, new FileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir,
                                    BasicFileAttributes attrs)
                                    throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path file,
                                    BasicFileAttributes attrs)
                                    throws IOException {
                                if (file.toFile().isFile()) {
                                    Files.delete(file);
                                }
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFileFailed(Path file,
                                    IOException exc) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir,
                                    IOException exc) throws IOException {
                                Files.delete(dir);
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    }
                }
            }
        } // else return null;
    }

}
