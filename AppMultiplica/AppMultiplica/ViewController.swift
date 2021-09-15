//
//  ViewController.swift
//  AppMultiplica
//
//  Created by Adalto Selau Sparremberger on 03/08/21.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet weak var txtValor: UITextField!
    
    @IBOutlet weak var lblResultado: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }


    @IBAction func calcular(_ sender: Any) {
//        let sValor = txtValor.text
//        let valor = Double( sValor! )
//        let resultado = valor! * 2
        
        if let sValor = txtValor.text{
            if let valor = Double( sValor ){
                let resultado = valor * 2
                lblResultado.text = String( resultado )
            }
        }
        
        
        
    }
}

