package br.pro.adalto.applista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FormularioActivity extends AppCompatActivity {

    private EditText etNome;
    private Spinner spCategorias;
    private Button btnSalvar;
    private String acao;
    private Produto produto;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();


        etNome = findViewById(R.id.etNome);
        spCategorias = findViewById(R.id.spCategorias);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvar();
            }
        });

        acao = getIntent().getStringExtra("acao");
        if( acao.equals("editar")){
            carregarFormulario();
        }


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

    private void carregarFormulario(){
        String idProduto = getIntent().getStringExtra("idProduto");

        produto = new Produto();
        produto.setId(  idProduto  );

        produto.setNome(  getIntent().getStringExtra("nome") );
        produto.setCategoria( getIntent().getStringExtra("categoria") );


        etNome.setText( produto.getNome() );

        String[] categorias = getResources().getStringArray(R.array.categorias);

        for( int i = 0; i < categorias.length ; i++){
            if( produto.getCategoria().equals( categorias[i]  )){
                spCategorias.setSelection( i );
                break;
            }
        }

    }


    private void salvar(){
        String nome = etNome.getText().toString();

        if( nome.isEmpty() || spCategorias.getSelectedItemPosition() == 0 ){
            Toast.makeText(this, "Você deve preencher todos os campos!", Toast.LENGTH_LONG).show();
        }else {

            if(  acao.equals("inserir") ) {
                produto = new Produto();
            }
            produto.setNome( nome );
            produto.setCategoria( spCategorias.getSelectedItem().toString()  );
            produto.setIdUsuario(  auth.getCurrentUser().getUid()  );

            if(  acao.equals("inserir") ) {

                reference.child("produtos").push().setValue( produto );

                etNome.setText("");
                spCategorias.setSelection(0, true);
            }else{
                String idProduto = produto.getId();
                produto.setId( null );
                reference.child("produtos").child( idProduto ).setValue( produto );
                finish();
            }

        }

    }

}