package br.pro.adalto.applista;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lvProdutos;
    private List<Produto> listaDeProdutos;
    private ArrayAdapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private ChildEventListener childEventListener;
    private Query query;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, FormularioActivity.class);
                intent.putExtra("acao", "inserir");
                startActivity( intent );
            }
        });

        lvProdutos = findViewById(R.id.lvProdutos);

        listaDeProdutos = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaDeProdutos);
        lvProdutos.setAdapter(adapter);

        lvProdutos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent( MainActivity.this, FormularioActivity.class);
                intent.putExtra("acao", "editar");
                Produto prodSelececionado = listaDeProdutos.get(position);
                intent.putExtra("idProduto", prodSelececionado.getId() );
                intent.putExtra("nome", prodSelececionado.getNome() );
                intent.putExtra("categoria", prodSelececionado.getCategoria());
                startActivity( intent );
            }
        });

        lvProdutos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                excluir( position );

                return true;
            }
        });

   //     carregarLista();

        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if( auth.getCurrentUser() == null ){
                    finish();
                }
            }
        };
        auth.addAuthStateListener( authStateListener );

    }

    @Override
    protected void onStart() {
        super.onStart();

        listaDeProdutos.clear();

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        query = reference.child("produtos").orderByChild("nome");

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    String idUserProd = snapshot.child("idUsuario").getValue(String.class);

                    if(  idUserProd.equals(  auth.getCurrentUser().getUid() ) ) {

                        Produto prod = new Produto();
                        prod.setId(snapshot.getKey());
                        prod.setNome(snapshot.child("nome").getValue(String.class));
                        prod.setCategoria(snapshot.child("categoria").getValue(String.class));
                        prod.setIdUsuario( idUserProd );

                        listaDeProdutos.add(prod);
                        adapter.notifyDataSetChanged();

                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (Produto p: listaDeProdutos) {
                    if ( p.getId().equals(  snapshot.getKey() ) ){
                        p.setNome( snapshot.child("nome").getValue(String.class) );
                        p.setCategoria( snapshot.child("categoria").getValue(String.class) );
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (Produto p: listaDeProdutos) {
                    if ( p.getId().equals(  snapshot.getKey() ) ){
                        listaDeProdutos.remove( p );
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        query.addChildEventListener(  childEventListener );

}


    @Override
    protected void onStop() {
        super.onStop();

        query.removeEventListener( childEventListener );
    }

    private void excluir(int posicao){
        Produto prodSelecionado = listaDeProdutos.get( posicao );
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Excluir");
        alerta.setIcon( android.R.drawable.ic_delete);
        alerta.setMessage("Confirma a exclusão do produto " + prodSelecionado.getNome() + "? ");

        alerta.setNeutralButton("Cancelar", null);
  //    alerta.setNegativeButton("Não", null);

        alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reference.child("produtos").child( prodSelecionado.getId() ).removeValue();
            }
        });
        alerta.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        carregarLista();
    }

    private void carregarLista(){

   //     listaDeProdutos = ProdutoDAO.getProdutos(this);




        if( listaDeProdutos.size() == 0 ){
            Produto fake = new Produto("Lista Vazia ", "");
            listaDeProdutos.add( fake );
            lvProdutos.setEnabled(false);
        }else {
            lvProdutos.setEnabled(true);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.add( "Novo item" );
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuSair) {
            auth.signOut();
        }

        if( id == R.id.menuAddProduto){
            Intent intent = new Intent( MainActivity.this, FormularioActivity.class);
            intent.putExtra("acao", "inserir");
            startActivity( intent );
        }

        return super.onOptionsItemSelected(item);
    }

}