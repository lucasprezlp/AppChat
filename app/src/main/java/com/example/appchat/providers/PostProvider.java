package com.example.appchat.providers;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.appchat.model.Post;
import com.example.appchat.model.User;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class PostProvider {

    public LiveData<String> addPost(Post post) {
        MutableLiveData<String> result = new MutableLiveData<>();
        post.put("titulo", post.getTitulo());
        post.put("descripcion", post.getDescripcion());
        post.put("duracion", post.getDuracion());
        post.put("categoria", post.getCategoria());
        post.put("presupuesto", post.getPresupuesto());
        ParseUser currentUser = ParseUser.getCurrentUser();
        post.put("user", currentUser);
        post.saveInBackground(e -> {
            if (e == null) {
                ParseRelation<ParseObject> relation = post.getRelation("images");
                for (String url : post.getImagenes()) {
                    ParseObject imageObject = new ParseObject("Image");
                    imageObject.put("url", url);
                    imageObject.saveInBackground(imgSaveError -> {
                        if (imgSaveError == null) {
                            relation.add(imageObject);
                            post.saveInBackground(saveError -> {
                                if (saveError == null) {
                                    result.setValue("Post publicado");
                                } else {
                                    result.setValue("Error al guardar la relación con las imágenes: " + saveError.getMessage());
                                }
                            });
                        } else {
                            result.setValue("Error al guardar la imagen: " + imgSaveError.getMessage());
                        }
                    });
                }
            } else {
                result.setValue("Error al guardar el post: " + e.getMessage());
            }
        });

        return result;
    }

    public LiveData<List<Post>> getPostsByCurrentUser(int page) {
        MutableLiveData<List<Post>> result = new MutableLiveData<>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            result.setValue(new ArrayList<>());
            return result;
        }

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo("user", currentUser);
        query.include("user"); // Incluir la información del usuario
        query.orderByDescending("createdAt");

        query.findInBackground((posts, e) -> {
            if (e == null) {
                List<Post> postList = new ArrayList<>();
                for (ParseObject postObject : posts) {
                    Log.d("PostObject", "ID: " + postObject.getObjectId() + ", Title: " + postObject.getString("titulo"));

                    Post post = ParseObject.create(Post.class);
                    post.setObjectId(postObject.getObjectId());
                    post.setTitulo(postObject.getString("titulo"));
                    post.setDescripcion(postObject.getString("descripcion"));
                    post.setDuracion(postObject.getInt("duracion"));
                    post.setCategoria(postObject.getString("categoria"));
                    post.setPresupuesto(postObject.getDouble("presupuesto"));

                    // Obtener imágenes
                    ParseRelation<ParseObject> relation = postObject.getRelation("images");
                    try {
                        List<ParseObject> images = relation.getQuery().find();
                        List<String> imageUrls = new ArrayList<>();
                        for (ParseObject imageObject : images) {
                            imageUrls.add(imageObject.getString("url"));
                        }
                        post.setImagenes(imageUrls);
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }

                    // Mapeo del usuario
                    ParseUser parseUser = postObject.getParseUser("user");
                    if (parseUser != null) {
                        try {
                            parseUser.fetchIfNeeded();
                            User user = ParseObject.createWithoutData(User.class, parseUser.getObjectId());
                            user.setUsername(parseUser.getUsername());
                            user.setEmail(parseUser.getEmail());
                            user.setFotoperfil(parseUser.getString("fotoperfil"));
                            user.setRedSocial(parseUser.getString("redSocial"));

                            post.setUser(user); // Asignar el usuario convertido al post
                        } catch (ParseException parseException) {
                            Log.e("FetchUserError", "Error al obtener el usuario: ", parseException);
                        }
                    } else {
                        Log.d("UserPointer", "User pointer es null");
                    }

                    postList.add(post);
                }
                result.setValue(postList);
            } else {
                result.setValue(new ArrayList<>());
                Log.e("ParseError", "Error al recuperar los posts del usuario actual: ", e);
            }
        });

        return result;
    }


    public LiveData<List<Post>> getAllPosts() {
        MutableLiveData<List<Post>> result = new MutableLiveData<>();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("user"); // Asegúrate de incluir el usuario relacionado
        query.findInBackground((posts, e) -> {
            if (e == null) {
                List<Post> postList = new ArrayList<>();
                for (ParseObject postObject : posts) {
                    Log.d("PostObject", "ID: " + postObject.getObjectId() + ", Title: " + postObject.getString("titulo"));

                    Post post = ParseObject.create(Post.class);
                    post.setObjectId(postObject.getObjectId());
                    post.setTitulo(postObject.getString("titulo"));
                    post.setDescripcion(postObject.getString("descripcion"));
                    post.setDuracion(postObject.getInt("duracion"));
                    post.setCategoria(postObject.getString("categoria"));
                    post.setPresupuesto(postObject.getDouble("presupuesto"));

                    // Obtener imágenes
                    ParseRelation<ParseObject> relation = postObject.getRelation("images");
                    try {
                        List<ParseObject> images = relation.getQuery().find();
                        List<String> imageUrls = new ArrayList<>();
                        for (ParseObject imageObject : images) {
                            imageUrls.add(imageObject.getString("url"));
                        }
                        post.setImagenes(imageUrls);
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }

                    // Mapeo del usuario
                    ParseUser parseUser = postObject.getParseUser("user");
                    if (parseUser != null) {
                        try {
                            parseUser.fetchIfNeeded();
                            User user = ParseObject.createWithoutData(User.class, parseUser.getObjectId());
                            user.setUsername(parseUser.getUsername());
                            user.setEmail(parseUser.getEmail());
                            user.setFotoperfil(parseUser.getString("fotoperfil"));
                            user.setRedSocial(parseUser.getString("redSocial"));

                            post.setUser(user); // Asignar el usuario convertido al post
                        } catch (ParseException parseException) {
                            Log.e("FetchUserError", "Error al obtener el usuario: ", parseException);
                        }
                    } else {
                        Log.d("UserPointer", "User pointer es null");
                    }

                    postList.add(post);
                }
                result.setValue(postList);
            } else {
                result.setValue(new ArrayList<>());
                Log.e("ParseError", "Error al recuperar todos los posts: ", e);
            }
        });

        return result;
    }


    public LiveData<String> deletePost(String postId) {
        MutableLiveData<String> result = new MutableLiveData<>();

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.getInBackground(postId, (post, e) -> {
            if (e == null) {
                post.deleteInBackground(e1 -> {
                    if (e1 == null) {
                        Log.d("PostDelete", "Post eliminado con éxito.");
                        result.postValue("Post eliminado correctamente");
                    } else {
                        Log.e("PostDelete", "Error al eliminar el post: ", e1);
                        result.postValue("Error al eliminar el post: " + e1.getMessage());
                    }
                });
            } else {
                Log.e("PostDelete", "Error al encontrar el post: ", e);
                result.postValue("Error al encontrar el post: " + e.getMessage());
            }
        });

        return result;
    }


    public LiveData<Post> getPostDetail(String postId) {
        MutableLiveData<Post> result = new MutableLiveData<>();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("user");
        query.include("images");
        query.getInBackground(postId, (post, e) -> {
            if (e == null) {
                ParseRelation<ParseObject> relation = post.getRelation("images");
                try {
                    List<ParseObject> images = relation.getQuery().find();
                    List<String> imageUrls = new ArrayList<>();
                    for (ParseObject imageObject : images) {
                        imageUrls.add(imageObject.getString("url"));
                    }
                    post.setImagenes(imageUrls);
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                ParseObject userObject = post.getParseObject("user");
                if (userObject != null) try {
                    userObject.fetchIfNeeded();
                    User user = new User();
                    user.setUsername(userObject.getString("username"));
                    user.setEmail(userObject.getString("email"));
                    user.setFotoperfil(userObject.getString("foto_perfil"));

                    post.setUser(user);
                } catch (ParseException userFetchException) {
                    userFetchException.printStackTrace();
                }
                else {
                    Log.w("PostDetail", "El usuario asociado al post es nulo.");
                }

                result.setValue(post);
            } else {
                Log.e("ParseError", "Error al obtener el post: ", e);
                result.setValue(null);
            }
        });

        return result;
    }


    public interface CommentsCallback {
        void onSuccess(List<ParseObject> comments);
        void onFailure(Exception e);
    }

    public void fetchComments(String postId, CommentsCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comentario");
        query.whereEqualTo("post", ParseObject.createWithoutData("Post", postId));
        query.include("user"); // Incluye los datos del usuario en la consulta
        query.findInBackground((comentarios, e) -> {
            if (e == null) {
                callback.onSuccess(comentarios);
            } else {
                callback.onFailure(e);
            }
        });
    }

    public void saveComment(String postId, String commentText, ParseUser currentUser, SaveCallback callback) {
        ParseObject post = ParseObject.createWithoutData("Post", postId);

        ParseObject comentario = ParseObject.create("Comentario");
        comentario.put("texto", commentText);
        comentario.put("post", post);
        comentario.put("user", currentUser);

        comentario.saveInBackground(callback);
    }

}






